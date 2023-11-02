package uk.ac.ed.epcc.webapp.session.twofactor;

import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.function.Consumer;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.CurrentTimeService;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.model.data.DataObject;

/** Interface for classes (usually composites) that implement TOTP code verification.
 * Most of the TOTP logic is implemented as default methods to avoid code duplication.
 * It might be better to extract the logic to its own class
 * 
 * @param <X>
 */
public interface TotpProvider<X extends DataObject> extends Contexed {
   public  String getConfigPrefix();
   public static final String ALG="HmacSHA1";
   /** number of milliseconds per code value
	 * 
	 * @return
	 */
	default public long getNorm() {
		return getContext().getLongParameter(getConfigPrefix()+"refresh.millis", 30000L); // 30 second
	}
	default public int getWindow() {
		return getContext().getIntegerParameter(getConfigPrefix()+"window",3);
	}
	default public int getMaxFail() {
		return getContext().getIntegerParameter(getConfigPrefix()+"max_fail",100);
	}
	
	default public long getCode(Key key,long counter) throws NoSuchAlgorithmException, InvalidKeyException {
		Mac mac = Mac.getInstance(ALG);
		mac.init(key);
		ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(0, counter);

        byte[] hmac = mac.doFinal(buffer.array());
        int offset = hmac[hmac.length - 1] & 0x0f;
        int number = 0;
        for (int i = 0; i < 4; i++) {
            // Note that we're re-using the first four bytes of the buffer here; we just ignore the latter four from
            // here on out.
        	number <<=8;
        	number |= ( hmac[i+offset] & 0xff);
        }

        final int hotp = number & 0x7fffffff;

        return hotp % 1000000;
		
	}
	default public Key makeNewKey() throws NoSuchAlgorithmException {
		KeyGenerator gen = KeyGenerator.getInstance(ALG);
		gen.init(128);
		return gen.generateKey();
	}
	default public Key decodeKey(String enc) {
    	if( enc == null || enc.isEmpty()) {
    		return null;
    	}
    	return new SecretKeySpec(Base32.decode(enc), ALG);
    }
	public static String getEncodedSecret(Key key) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeySpecException {
		if( key == null) {
			return null;
		}
		String encode = Base32.encode(key.getEncoded());
		return encode;
	}
	public String getMFASecret(X user);
	default public Key getSecret(X user) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeySpecException {
		String secret = getMFASecret(user);
		if(secret == null || secret.length() == 0) {
			return null;
		}
		return decodeKey(secret);
	}
	
	public void setMFASecret(X user, String secret);
	default public void setSecret(X user, Key key) {
		if( key == null) {
			clearSecret(user);
			return;
		}
		
		String secret = Base32.encode(key.getEncoded());
		setMFASecret(user, secret);
	}
	public void clearSecret(X user);
	
	default public void setSecret(X user, String enc) {
		if( enc == null || enc.isEmpty()) {
			clearSecret(user);
			return;
		}
		setSecret(user, decodeKey(enc));
	}
		
	
	/** Get the timestamp/counter used for the last sucessful authentication. 
	 * 
	 * This is a long value corresponding to a java millisecond timestamp but
	 * rounded to the normalisation value. 
	 * If this field exists and is populated codes equal to or prior to this value will
	 * not be accepted (to make the codes strictly one-time).
	 * @param user
	 * @return
	 */
	public long getLastUsed(X user);
	
	public void setLastUsed(X user, long time);
	/** Set the counter value (not the time stamp) or a successful authentication.
	 * 
	 * @param user
	 * @param counter
	 */
	default public  void setLastUsedCounter(X user, long counter) {
		if( user == null) {
			return;
		}
		setLastUsed(user,Long.valueOf(counter * getNorm()));
	}
	/** Get he number of times a bad code has been provided
	 * (if tracked by the implementing class).
	 * 
	 * @param target
	 * @return
	 */
	public int getFailCount(X target);
	/** reset the fail count after a successful verification.
	 * 
	 * @param target
	 */
	public void clearFailCount(X target);
	/** record an additional verification fail.
	 * At the very least this should increment the fail counter but it may
	 * trigger other side-effects such as notification emails when max-fail is exceeded.
	 * 
	 * @param target
	 */
	public void doFail(X target);
	
	
	/** Validate a value against a key.
	 * 
	 * If the user parameter is not null the last-used and fail-count value are also checked.
	 * 
	 * If verification is successful the matching counter value is provided to the used {@link Consumer} this can
	 * be used to update {@link #setLastUsed(DataObject, long)} but the update may need to be delayed
	 * if {@link #verify(DataObject, Key, Integer, Consumer, StringBuilder)} is going to need to be
	 * called multiple times in a workflow. 
	 * 
	 * @param user  target object
	 * @param key   Key to verify against
	 * @param value code the check
	 * @param used  optional {@link Consumer} to receive counter value that was verified
	 * @param notes optional {@link StringBuilder} to receive additional feedback notes for the user
	 * @return
	 */
	default public boolean verify(X user,Key key,Integer value,Consumer<Long> used,StringBuilder notes) {
		Logger logger = getLogger();
		if( key == null) {
			logger.debug("No key allow login");
			return true;
		}
		CurrentTimeService serv = getContext().getService(CurrentTimeService.class);
		Date currentTime = serv.getCurrentTime();
		long counter = currentTime.getTime();
		long norm = getNorm();
		counter = counter / norm;
		long last_counter = getLastUsed(user) / norm;
		int window = getWindow();
		
		for(int i=-(window-1)/2 ; i<= window/2 ; ++i) {
			try {
				long code = getCode(key, counter+i);
				logger.debug("i="+i+" code="+code+" value="+value);
				if( value.longValue() == code) {
					if( (counter+i) > last_counter ) {
						if(user != null  ) {
							// code is ok but check fail count
							if( getFailCount(user) > getMaxFail() && getMaxFail() > 0) {
								logger.error("User exceeded MFA fail count "+user.getIdentifier());
								return false;
							}
							clearFailCount(user);
							if(used != null) {
								used.accept(counter+i);
							}
						}
						return true;
					}else {
						logger.error("Code re-use for "+user.getIdentifier()+" "+(counter+i)+"<"+last_counter);
						if( notes != null) {
							notes.append("This code has already been used once");
						}
						return false;
					}
				}
			} catch (Exception e) {
				logger.error("Error checking code", e);
			}
		}
		logger.debug("code does not match");
		if( user != null) {
			doFail(user);
		}
		return false;
	}
	public String getLocation(X user) throws DataException;
    public String getImageURL(X user);
    
   public String getName(X user);
	
	default public URI getURI(X user,Key key) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeySpecException, URISyntaxException, DataException {
		
		String location = getLocation(user);
		StringBuilder sb = new StringBuilder();
		sb.append("otpauth://totp/");
		sb.append(URLEncoder.encode(location,"UTF-8"));
		sb.append(":");
	
		String name = getName(user);
		name=URLEncoder.encode(name.trim(), "UTF-8");
		sb.append(name);
		sb.append("?secret=");
		sb.append(TotpProvider.getEncodedSecret(key));
		
		String issuer = getContext().getExpandedProperty("website-name");
		if( issuer != null) {
			sb.append("&issuer=");
			sb.append(URLEncoder.encode(issuer, "UTF-8"));
		}
		String image = getImageURL(user);
		if( image != null && ! image.isEmpty()) {
			sb.append("&image=");
			sb.append(URLEncoder.encode(image, "UTF-8"));
		}
		return new URI(sb.toString());
	}
}
