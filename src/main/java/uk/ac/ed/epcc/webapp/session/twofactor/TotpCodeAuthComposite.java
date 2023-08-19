//| Copyright - The University of Edinburgh 2018                            |
//|                                                                         |
//| Licensed under the Apache License, Version 2.0 (the "License");         |
//| you may not use this file except in compliance with the License.        |
//| You may obtain a copy of the License at                                 |
//|                                                                         |
//|    http://www.apache.org/licenses/LICENSE-2.0                           |
//|                                                                         |
//| Unless required by applicable law or agreed to in writing, software     |
//| distributed under the License is distributed on an "AS IS" BASIS,       |
//| WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.|
//| See the License for the specific language governing permissions and     |
//| limitations under the License.                                          |
package uk.ac.ed.epcc.webapp.session.twofactor;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;



import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.CurrentTimeService;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.content.ExtendedXMLBuilder;
import uk.ac.ed.epcc.webapp.content.PreDefinedContent;
import uk.ac.ed.epcc.webapp.email.Emailer;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.action.FormAction;
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.forms.exceptions.FatalTransitionException;
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.forms.html.RedirectResult;
import uk.ac.ed.epcc.webapp.forms.inputs.ConstantInput;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.inputs.IntegerInput;
import uk.ac.ed.epcc.webapp.forms.result.ChainedTransitionResult;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.MessageResult;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractDirectTransition;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractFormTransition;
import uk.ac.ed.epcc.webapp.forms.transition.DirectTransition;
import uk.ac.ed.epcc.webapp.forms.transition.ExtraContent;
import uk.ac.ed.epcc.webapp.forms.transition.Transition;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory;
import uk.ac.ed.epcc.webapp.jdbc.table.*;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.model.AnonymisingComposite;
import uk.ac.ed.epcc.webapp.model.SummaryContributer;
import uk.ac.ed.epcc.webapp.model.data.Repository;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.filter.FilterAdd;
import uk.ac.ed.epcc.webapp.servlet.QRServlet;
import uk.ac.ed.epcc.webapp.servlet.ServletService;
import uk.ac.ed.epcc.webapp.servlet.navigation.NavigationMenuService;
import uk.ac.ed.epcc.webapp.session.*;
import uk.ac.ed.epcc.webapp.validation.FieldValidator;

/**  A {@link FormAuthComposite} that implements time-based authentication codes compatible
 * with google authenticator
 * <ul>
 * <li>https://tools.ietf.org/html/rfc4226</li>
 * <li>https://tools.ietf.org/html/rfc6238</li>
 * </ul>
 * @author Stephen Booth
 *
 */
public class TotpCodeAuthComposite<A extends AppUser> extends CodeAuthComposite<A,Integer> implements AppUserTransitionContributor, SummaryContributer<A>, RequiredPageProvider<A>, AnonymisingComposite<A> {
	private static final int MAX_NORMAL_CODE = 999999;
	private static final int MAX_RECOVERY_CODE = 99999999;
	public static final Feature REQUIRED_TWO_FACTOR= new Feature("two_factor.required",false,"Is two factor authentication required");
	public static final Feature VERIFY_OLD_CODE=new Feature("two_factor.verify_previous_code",true,"Verify current code on key change");
	private static final String SECRET_FIELD="AuthCodeSecret";
	private static final String LAST_USED_FIELD="LastAuthCodeUsed";
	private static final String FAIL_COUNT="AuthCodeFails";
	public static final String REMOVE_KEY_ROLE="RemoveTwoFactor";
	public static final String MAKE_RECOVERY_CODE_ROLE="MakeRecoveryCode";
	public static final String USED_COUNTER_ATTR="UsedCounter";
	public static final String RECOVERY_FIELD="AuthCodeRecover";
	
	public static final Feature NOTIFY_MFA_LOCK = new Feature("mfa.notify-lock",true,"Notify by email if maximum mfa attempts are exceeded");

	/**
	 * @param fac
	 */
	public TotpCodeAuthComposite(AppUserFactory<A> fac,String tag) {
		super(fac,tag);
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.twofactor.CodeAuthComposite#enabled(uk.ac.ed.epcc.webapp.session.AppUser)
	 */
	@Override
	public boolean enabled(A user) {
		if( enabled()) {
			try {
				return hasKey(user);
			} catch (Exception e) {
				getLogger().error("Error getting key", e);
			}
		}
		return false;
	}

	/**
	 * @return
	 */
	public boolean enabled() {
		return getRepository().hasField(SECRET_FIELD);
	}

	/** Do we have a key for this user.
	 * @param user
	 * @return boolean
	 */
	public boolean hasKey(A user){
		try {
			return getSecret(user) != null;
		} catch (Exception e) {
			getLogger().error("Error checking for key", e);
			return false;
		}
	}
	
	public Key makeNewKey() throws NoSuchAlgorithmException {
		KeyGenerator gen = KeyGenerator.getInstance(ALG);
		gen.init(128);
		return gen.generateKey();
	}
	
	public void setSecret(A user, Key key) {
		if( key == null) {
			clearSecret(user);
			return;
		}
		
		getRecord(user).setProperty(SECRET_FIELD, Base32.encode(key.getEncoded()));
	}
	public void setSecret(A user, String enc) {
		if( enc == null || enc.isEmpty()) {
			clearSecret(user);
			return;
		}
		setSecret(user, decodeKey(enc));
	}

	/**
	 * @param user
	 */
	public void clearSecret(A user) {
		Record record = getRecord(user);
		record.setProperty(SECRET_FIELD, null);
		record.setOptionalProperty(RECOVERY_FIELD, null);
	}
	
    public Key decodeKey(String enc) {
    	if( enc == null || enc.isEmpty()) {
    		return null;
    	}
    	return new SecretKeySpec(Base32.decode(enc), ALG);
    }
	public Key getSecret(A user) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeySpecException {
		String secret = getRecord(user).getStringProperty(SECRET_FIELD);
		if(secret == null || secret.length() == 0) {
			return null;
		}
		return decodeKey(secret);
	}
	public int getRecoveryCode(A user) {
		return getRecord(user).getIntProperty(RECOVERY_FIELD, -1);
	}
	public void setRecoveryCode(A user, int code) {
		getRecord(user).setProperty(RECOVERY_FIELD, code);
	}
	public String getEncodedSecret(Key key) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeySpecException {
		if( key == null) {
			return null;
		}
		String encode = Base32.encode(key.getEncoded());
		return encode;
	}
	private static final String ALG="HmacSHA1";
	
	
	public long getCode(Key key,long counter) throws NoSuchAlgorithmException, InvalidKeyException {
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
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.twofactor.CodeAuthComposite#getInput()
	 */
	@Override
	public Input<Integer> getInput() {
		IntegerInput input = new IntegerInput();
		input.setMin(0);
		if(useRecoveryCode()) {
			input.setMax(MAX_RECOVERY_CODE);
		}else {
			input.setMax(MAX_NORMAL_CODE);
		}
		input.setBoxWidth(8); // use longer box as many browsers reduce size with added number picker
		return input;
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
	public long getLastUsed(A user) {
		if( user == null) {
			return 0L;
		}
		return getRecord(user).getLongProperty(LAST_USED_FIELD,0L);
	}
	
	/** Set the counter value (not the time stamp) or a successful authentication.
	 * 
	 * @param user
	 * @param counter
	 */
	public void setLastUsedCounter(A user, long counter) {
		if( user == null) {
			return;
		}
		getRecord(user).setOptionalProperty(LAST_USED_FIELD, Long.valueOf(counter * getNorm()));
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.twofactor.CodeAuthComposite#verify(java.lang.Object)
	 */
	@Override
	public boolean verify(A user,Integer value) {
		try {
			int recovery = getRecoveryCode(user);
			if( recovery > 999999 && value.intValue() > MAX_NORMAL_CODE) {
				if( recovery == value.intValue() ) {
					clearSecret(user);
					user.commit();
					try {
						Emailer m = Emailer.getFactory(getContext());
						m.userNotification(user,"mfa_used_recovery_token.txt");
					} catch (Exception e) {
						getLogger().error("Failed to notify of use of recovery token", e);
					}
					return true;
				}else {
					doFail(user);
				}
			}
			return verify(user,getSecret(user),value);
		} catch (Exception e) {
			getLogger().error("Error getting secret", e);
			return false;
		}
	}
	/** Validate a value against a key.
	 * 
	 * If the user parameter is not null the last used value is also checked.
	 * 
	 * @param user
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean verify(A user,Key key,Integer value) {
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
							// Don't set the authenticated time here as
							// verification may be called multiple times
							// remember it in the AppContext
							getContext().setAttribute(USED_COUNTER_ATTR, (counter+i));
						}
						return true;
					}else {
						logger.error("Code re-use for "+user.getIdentifier()+" "+(counter+i)+"<"+last_counter);
						return false;
					}
				}
			} catch (Exception e) {
				logger.error("Error checking code", e);
			}
		}
		logger.debug("code does not match");
		doFail(user);
		return false;
	}
	/** number of milliseconds per code value
	 * 
	 * @return
	 */
	public long getNorm() {
		return getContext().getLongParameter("totp.refresh.millis", 30000L); // 30 second
	}
	public int getWindow() {
		return getContext().getIntegerParameter("totp.window",3);
	}
	public int getMaxFail() {
		return getContext().getIntegerParameter("totp.max_fail",100);
	}
	public boolean useRecoveryCode() {
		return getRepository().hasField(RECOVERY_FIELD);
	}
	@Override
	public TableSpecification modifyDefaultTableSpecification(TableSpecification spec, String table) {
		spec.setOptionalField(SECRET_FIELD, new StringFieldType(true, null, 32));
		spec.setOptionalField(LAST_USED_FIELD, new LongFieldType(true, null));
		spec.setOptionalField(FAIL_COUNT, new IntegerFieldType(true, 0));
		spec.setOptionalField(RECOVERY_FIELD, new IntegerFieldType(true, null));
		return spec;
	}

	@Override
	public Set<String> addSuppress(Set<String> suppress) {
		suppress.add(SECRET_FIELD);
		suppress.add(LAST_USED_FIELD);
		suppress.add(FAIL_COUNT);
		suppress.add(RECOVERY_FIELD);
		return suppress;
	}

	public URI getURI(A user,Key key) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeySpecException, URISyntaxException {
		StringBuilder sb = new StringBuilder();
		sb.append("otpauth://totp/");
		sb.append(URLEncoder.encode(getContext().getInitParameter("service.name"),"UTF-8"));
		sb.append(":");
		AppUserFactory<A> factory = (AppUserFactory<A>) getFactory();
		String name = factory.getCanonicalName(user);
		name=URLEncoder.encode(name.trim(), "UTF-8");
		sb.append(name);
		sb.append("?secret=");
		sb.append(getEncodedSecret(key));
		
		String issuer = getContext().getExpandedProperty("website-name");
		if( issuer != null) {
			sb.append("&issuer=");
			sb.append(URLEncoder.encode(issuer, "UTF-8"));
		}
		return new URI(sb.toString());
	}
	public class CodeValidator implements FieldValidator<Integer>{
		/**
		 * @param key
		 */
		public CodeValidator(Key key) {
			super();
			this.key = key;
		}

		private final Key key;

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.FieldValidator#validate(java.lang.Object)
		 */
		@Override
		public void validate(Integer data) throws FieldException {
			if( ! verify(null,key, data) ) {
				throw new ValidateException("Incorrect");
			}
			
		}
	}
	public class SetToptTransition extends AbstractFormTransition<A> implements ExtraContent<A>{
		/**
		 * @param prov
		 */
		public SetToptTransition(AppUserTransitionProvider prov) {
			super();
			this.prov = prov;
		}

		private final AppUserTransitionProvider prov;
		/**
		 * 
		 */
	    static final String NEW_AUTH_KEY_ATTR = "NewAuthKey";
		/**
		 * 
		 */
		static final String NEW_CODE = "NewCode";
		/**
		 * 
		 */
		static final String KEY = "Key";
		private Key new_key=null;
	
		public Key getKey() throws NoSuchAlgorithmException {
			if( new_key==null) {
				SessionService service = getContext().getService(SessionService.class);
				String text= (String) service.getAttribute(NEW_AUTH_KEY_ATTR);
				if( text != null) {
					new_key= decodeKey(text);
				}else {
					new_key=makeNewKey();
					try {
						service.setAttribute(NEW_AUTH_KEY_ATTR, getEncodedSecret(new_key));
					} catch (Exception e) {
						getLogger().error("Error encoding new key", e);
					}
				}
					
				
			}
			return new_key;
		}
		public class setKeyAction extends FormAction{
			private final AppUserTransitionProvider prov;
			/**
			 * @param user
			 */
			public setKeyAction(AppUserTransitionProvider prov,A user) {
				super();
				this.prov=prov;
				this.user = user;
			}
			private final A user;
			/* (non-Javadoc)
			 * @see uk.ac.ed.epcc.webapp.forms.action.FormAction#action(uk.ac.ed.epcc.webapp.forms.Form)
			 */
			@Override
			public FormResult action(Form f) throws ActionException {
				boolean had_key = hasKey(user);
				try {
					setSecret(user, (String)f.get(KEY));
					clearFailCount(user);
					user.commit();
				} catch (DataFault e) {
					throw new ActionException("Error setting key", e);
				}
				getContext().getService(SessionService.class).removeAttribute(NEW_AUTH_KEY_ATTR);
				resetNavigation();
				if( REQUIRED_TWO_FACTOR.isEnabled(getContext()) && ! had_key) {
					// probably a mandatory page
					return new RedirectResult("/main.jsp");
				}
				return prov.new ViewResult(user);
			}
			
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.transition.BaseFormTransition#buildForm(uk.ac.ed.epcc.webapp.forms.Form, java.lang.Object, uk.ac.ed.epcc.webapp.AppContext)
		 */
		@Override
		public void buildForm(Form f, A target, AppContext conn) throws TransitionException {
			try {
				boolean verify = VERIFY_OLD_CODE.isEnabled(conn) && needAuth(target);
				if( verify) {
					modifyForm(target, f);
				}
				Key key2 = getKey();
				String enc = getEncodedSecret(key2);
				f.addInput(KEY, new ConstantInput<>(enc,enc));
				f.addInput(NEW_CODE,  getInput());
				f.getField(NEW_CODE).addValidator(new CodeValidator(key2));
				if( ! verify ) {
					// will be single field
					f.setAutoFocus(NEW_CODE);
				}
				f.addAction("Set", new setKeyAction(prov,target));
			} catch (Exception e) {
				getLogger().error("Error building form", e);
				throw new TransitionException("Internal error");
			}
			
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.transition.ExtraContent#getExtraHtml(uk.ac.ed.epcc.webapp.content.ContentBuilder, uk.ac.ed.epcc.webapp.session.SessionService, java.lang.Object)
		 */
		@Override
		public <X extends ContentBuilder> X getExtraHtml(X cb, SessionService<?> op, A target) {
			cb.addText("This will be your new 2-factor authorisation key. You will need a smart-phone app such as Google Authenticator or Microsoft Authenticator to generate the verification codes.");
			cb.addText("You need to supply a verification code now to install the key");
			if( VERIFY_OLD_CODE.isEnabled(getContext()) && needAuth(target)) {
				cb.addText("You are changing an existing key and will have to input the codes for both the current and the new key. You may find it easier to disable the existing key first, then return to this page to set a new key");
			}
			try {
				ServletService serv = getContext().getService(ServletService.class);
				if( serv != null) {
					serv.noCache();
					ExtendedXMLBuilder xml = cb.getText();
					//xml.addClass("qrcode");
					xml.open("img");
					xml.addClass("qrcode");
					URI uri=getURI(target, getKey());
					xml.attr("src",serv.encodeURL(QRServlet.getImageURL(getContext(), "code.png", uri.toString())));
					xml.attr("alt","QRCode to be read by smartphone");
					// no alt as we want to avoid the value being cached
					//xml.attr("alt",uri.toString());
					xml.close();
					xml.appendParent();
				}
			}catch(Exception e) {
				getLogger().error("Error making image", e);
			}
			return cb;
		}
		
	}
	public static final CurrentUserKey SET_KEY = new CurrentUserKey("SetKey","Set 2 factor token","Enable/change two factor authentication for this account") {
		
		@Override
		public boolean allowState(AppUser user, SessionService op) {
			TotpCodeAuthComposite comp = (TotpCodeAuthComposite) op.getLoginFactory().getComposite(FormAuthComposite.class);
			if( comp == null ) {
				return false;
			}
			return comp.enabled();
		}
	};
	public static final AppUserKey CLEAR_KEY = new CurrentUserKey("ClearKey","Disable 2 factor","Disable two factor authentication for this account ?") {
		
		@Override
		public boolean allowState(AppUser user, SessionService op) {
			if( REQUIRED_TWO_FACTOR.isEnabled(op.getContext())) {
				// role access allows removal of required key.
				// user will be forces to set on next login
				return op.hasRole(REMOVE_KEY_ROLE);
			}
			TotpCodeAuthComposite comp = (TotpCodeAuthComposite) op.getLoginFactory().getComposite(FormAuthComposite.class);
			if( comp == null || ! comp.hasKey(user)) {
				return false;
			}
			return true;
		}

		@Override
		public boolean allow(AppUser user, SessionService op) {
			return (op.hasRole(REMOVE_KEY_ROLE) || op.isCurrentPerson(user)) && allowState(user, op);
		}
	};
	public static final AppUserKey RECOVERY = new CurrentUserKey("RecoveryCode","Generate 2 factor recovery code","Generate a two-factor recovery code for this account ?") {
		
		@Override
		public boolean allowState(AppUser user, SessionService op) {
			if( REQUIRED_TWO_FACTOR.isEnabled(op.getContext())) {
				// role access allows removal of required key.
				// user will be forces to set on next login
				return op.hasRole(MAKE_RECOVERY_CODE_ROLE);
			}
			TotpCodeAuthComposite comp = (TotpCodeAuthComposite) op.getLoginFactory().getComposite(FormAuthComposite.class);
			if( comp == null || ! comp.hasKey(user)) {
				return false;
			}
			return true;
		}

		@Override
		public boolean allow(AppUser user, SessionService op) {
			return (op.hasRole(MAKE_RECOVERY_CODE_ROLE) || op.isCurrentPerson(user)) && allowState(user, op);
		}
	};
	public class DirectClearKeyTransition extends AbstractDirectTransition<A>{
		/**
		 * @param prov
		 */
		public DirectClearKeyTransition(AppUserTransitionProvider prov) {
			super();
			this.prov = prov;
		}
		private final AppUserTransitionProvider prov;
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.transition.DirectTransition#doTransition(java.lang.Object, uk.ac.ed.epcc.webapp.AppContext)
		 */
		@Override
		public FormResult doTransition(A target, AppContext c) throws TransitionException {
			try {
			clearSecret(target);
			target.commit();
			resetNavigation();
			return prov.new ViewResult(target);
			}catch(Exception t) {
				getLogger().error("Error clearing secret", t);
				throw new FatalTransitionException("Internal error");
			}
		}
		
	}
	public class RecoveryCodeTransition extends AbstractFormTransition<A> implements ExtraContent<A>{
		private static final int MAX_RECOVERY_CODE = 99999999;

		public RecoveryCodeTransition(AppUserTransitionProvider prov) {
			super();
			this.prov = prov;
		}

		private final AppUserTransitionProvider prov;
		
		@Override
		public void buildForm(Form f, A target, AppContext conn) throws TransitionException {
			FormAction set = new FormAction() {
				
				@Override
				public FormResult action(Form f) throws ActionException {
					try {
						RandomService rand = getContext().getService(RandomService.class);
						int new_code = rand.randomInt(MAX_RECOVERY_CODE-MAX_NORMAL_CODE-1)+1+MAX_NORMAL_CODE;
						setRecoveryCode(target, new_code);
						target.commit();
						try {
							Emailer m = Emailer.getFactory(getContext());
							m.userNotification(target,"mfa_new_recovery_token.txt");
						} catch (Exception e) {
							getLogger().error( "Failed to notify of new recovery token",e);
						}
						return new MessageResult("recovery_token_set", String.format("%08d", new_code));
					}catch(Exception e) {
						throw new ActionException("Error making recovery code", e);
					}
				}
			};
			f.addAction("CreateCode", set);
			FormAction cancel = new FormAction() {
				
				@Override
				public FormResult action(Form f) throws ActionException {
					return prov.new ViewResult(target);
				}
			};
			cancel.setMustValidate(false);
			f.addAction("Cancel", cancel);
			
		}

		@Override
		public <X extends ContentBuilder> X getExtraHtml(X cb, SessionService<?> op, A target) {
			cb.addText("Do you wish to create a single-use recovery code for this account?");
			if( getRecoveryCode(target) > MAX_NORMAL_CODE) {
				cb.addText("This will replace the existing recovery code");
			}
			return cb;
		}
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.AppUserTransitionContributor#getTransitions()
	 */
	@Override
	public Map<AppUserKey, Transition<AppUser>> getTransitions(AppUserTransitionProvider prov) {
		Map<AppUserKey,Transition<AppUser>> map = new LinkedHashMap<>();
		if( enabled()) {
			
			map.put(SET_KEY,(Transition<AppUser>) new SetToptTransition(prov));
			map.put(CLEAR_KEY,new AuthorisedConfirmTransition<AppUser,AppUser>("Disable 2-factor authentication", (DirectTransition<AppUser>) new DirectClearKeyTransition(prov), prov.new ViewTransition(),REMOVE_KEY_ROLE));
			if( useRecoveryCode()) {
				map.put(RECOVERY, (Transition<AppUser>) new RecoveryCodeTransition(prov));
			}
		}
		return map;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.RequiredPageProvider#getRequiredPages()
	 */
	@Override
	public Set<RequiredPage<A>> getRequiredPages() {
		HashSet<RequiredPage<A>> set = new HashSet<>();
		if( REQUIRED_TWO_FACTOR.isEnabled(getContext())) {
			set.add(new RequiredPage<A>() {

				@Override
				public boolean required(SessionService<A> sess) {
					A user = sess.getCurrentPerson();
					
					return user != null && ! hasKey(user);
				}

				@Override
				public FormResult getPage(SessionService<A> sess) {
					return new ChainedTransitionResult<>((TransitionFactory<AppUserKey, A>) AppUserTransitionProvider.getInstance(getContext()), sess.getCurrentPerson(), SET_KEY);
				}
			});
		}
		return set;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.SummaryContributer#addAttributes(java.util.Map, uk.ac.ed.epcc.webapp.model.data.DataObject)
	 */
	@Override
	public void addAttributes(Map<String, Object> attributes, A target) {
		if( enabled()) {
			//TODO should we restict access to this attribute.
			// admins may care but project managers should not.
			attributes.put("2-factor authentication", hasKey(target));
		}
	
		
	}
	private void resetNavigation() {
		NavigationMenuService nms = getContext().getService(NavigationMenuService.class);
		if( nms != null ){
			nms.resetMenu();
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.twofactor.FormAuthComposite#getConfigPrefix()
	 */
	@Override
	protected String getConfigPrefix() {
		return "totp_auth_code";
	}

	@Override
	public ContentBuilder addExtra(ContentBuilder cb) {
		cb = super.addExtra(cb);
		PreDefinedContent extra = new PreDefinedContent(getContext(), true, PreDefinedContent.DEFAULT_BUNDLE, "two_factor.extra_content");
		if( extra.hasContent()) {
			cb.addObject(extra);
		}
		return cb;
	}

	@Override
	public void anonymise(A target) {
		if( ! REQUIRED_TWO_FACTOR.isEnabled(getContext())) {
			clearSecret(target);
		}
		
	}

	@Override
	public void completeAuth(A target) {
		Long used = (Long) getContext().getAttribute(USED_COUNTER_ATTR);
		if( used != null) {
			setLastUsedCounter(target, used);
			try {
				target.commit();
			} catch (DataFault e) {
				getLogger().error("Error setting lastUsed", e);
			}
		}
	}

	public int getFailCount(A target) {
		return getRecord(target).getIntProperty(FAIL_COUNT, 0);
	}
	private void clearFailCount(A target) {
		if( target == null ) {
			return;
		}
		getRecord(target).setOptionalProperty(FAIL_COUNT, 0);
		try {
			target.commit();
		} catch (DataFault e) {
			getLogger().error("Error resetting fail count", e);
		}
	}
	private void doFail(A target) {
		if( ! getRepository().hasField(FAIL_COUNT)  || target == null) {
			return;
		}
		// increment value in database to reduce chance
		// of brute forcing by parallel streams
		FilterAdd<A> adder = new FilterAdd<>(getRepository());
		try {
			adder.update(getRepository().getNumberExpression(Integer.class, FAIL_COUNT), 1, getFactory().getFilter(target));
			Repository.Record r = getRecord(target);
			int new_fail = r.getIntProperty(FAIL_COUNT, 0)+1;
			r.setProperty(FAIL_COUNT, new_fail);
			setDirty(FAIL_COUNT, target, false);
			int max_fail = getMaxFail();
			if( new_fail > max_fail && new_fail < (max_fail+10)) {
				// limit to 10 emails per person
				// but send more than 1 in case there were parallel fails
				if( NOTIFY_MFA_LOCK.isEnabled(getContext())) {
					try {
						Emailer m = Emailer.getFactory(getContext());
						m.userNotification(target,"mfa_fails_exceeded.txt");
					} catch (Exception e) {
						getLogger().error("Failed to notify of mfa lock", e);
					}
				}
			}
		} catch (DataFault e) {
			getLogger().error("Error updating fail count",e);
		}
		
	}
}
