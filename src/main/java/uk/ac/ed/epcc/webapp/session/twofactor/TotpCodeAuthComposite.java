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

import org.apache.commons.codec.binary.Base32;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.CurrentTimeService;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.content.ExtendedXMLBuilder;
import uk.ac.ed.epcc.webapp.content.PreDefinedContent;
import uk.ac.ed.epcc.webapp.forms.FieldValidator;
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
import uk.ac.ed.epcc.webapp.forms.transition.AbstractDirectTransition;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractFormTransition;
import uk.ac.ed.epcc.webapp.forms.transition.DirectTransition;
import uk.ac.ed.epcc.webapp.forms.transition.ExtraContent;
import uk.ac.ed.epcc.webapp.forms.transition.Transition;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.model.SummaryContributer;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.servlet.QRServlet;
import uk.ac.ed.epcc.webapp.servlet.ServletService;
import uk.ac.ed.epcc.webapp.servlet.navigation.NavigationMenuService;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.AppUserFactory;
import uk.ac.ed.epcc.webapp.session.AppUserKey;
import uk.ac.ed.epcc.webapp.session.AppUserTransitionContributor;
import uk.ac.ed.epcc.webapp.session.AppUserTransitionProvider;
import uk.ac.ed.epcc.webapp.session.CurrentUserKey;
import uk.ac.ed.epcc.webapp.session.RequiredPage;
import uk.ac.ed.epcc.webapp.session.RequiredPageProvider;
import uk.ac.ed.epcc.webapp.session.SessionService;

/**  A {@link FormAuthComposite} that implements time-based authentication codes compatible
 * with google authenticator
 * <ul>
 * <li>https://tools.ietf.org/html/rfc4226</li>
 * <li>https://tools.ietf.org/html/rfc6238</li>
 * </ul>
 * @author Stephen Booth
 *
 */
public class TotpCodeAuthComposite<A extends AppUser> extends CodeAuthComposite<A,Integer> implements AppUserTransitionContributor, SummaryContributer<A>, RequiredPageProvider<A> {
	public static final Feature REQUIRED_TWO_FACTOR= new Feature("two_factor.required",false,"Is two factor authentication required");
	public static final Feature VERIFY_OLD_CODE=new Feature("two_factor.verify_previous_code",true,"Verify current code on key change");
	private static final String SECRET_FIELD="AuthCodeSecret";
	public static final String REMOVE_KEY_ROLE="RemoveTwoFactor";
	/**
	 * @param fac
	 */
	public TotpCodeAuthComposite(AppUserFactory<A> fac) {
		super(fac);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.twofactor.CodeAuthComposite#enabled(uk.ac.ed.epcc.webapp.session.AppUser)
	 */
	@Override
	protected boolean enabled(A user) {
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
		Base32 codec = new Base32(false);
		getRecord(user).setProperty(SECRET_FIELD, codec.encodeAsString(key.getEncoded()));
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
		getRecord(user).setProperty(SECRET_FIELD, null);
	}
    public Key decodeKey(String enc) {
    	if( enc == null || enc.isEmpty()) {
    		return null;
    	}
    	Base32 codec = new Base32();
    	return new SecretKeySpec(codec.decode(enc), ALG);
    }
	public Key getSecret(A user) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeySpecException {
		String secret = getRecord(user).getStringProperty(SECRET_FIELD);
		if(secret == null || secret.length() == 0) {
			return null;
		}
		return decodeKey(secret);
	}
	public String getEncodedSecret(Key key) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeySpecException {
		if( key == null) {
			return null;
		}
		Base32 codec = new Base32();
		String encode = codec.encodeToString(key.getEncoded());
		int pos = encode.indexOf('=');
		if( pos > -1) {
			encode=encode.substring(0, pos);
		}
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
		input.setMax(999999);
		input.setBoxWidth(8); // use longer box as many browsers reduce size with added number picker
		return input;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.twofactor.CodeAuthComposite#verify(java.lang.Object)
	 */
	@Override
	public boolean verify(A user,Integer value) {
		try {
			return verify(getSecret(user),value);
		} catch (Exception e) {
			getLogger().error("Error getting secret", e);
			return false;
		}
	}
	public boolean verify(Key key,Integer value) {
		Logger logger = getLogger();
		if( key == null) {
			logger.debug("No key allow login");
			return true;
		}
		CurrentTimeService serv = getContext().getService(CurrentTimeService.class);
		Date currentTime = serv.getCurrentTime();
		long counter = currentTime.getTime();
		counter = counter / getNorm();
		int window = getWindow();
		
		for(int i=-(window-1)/2 ; i<= window/2 ; ++i) {
			try {
				long code = getCode(key, counter+i);
				logger.debug("i="+i+" code="+code+" value="+value);
				if( value.longValue() == code) {
					return true;
				}
			} catch (Exception e) {
				logger.error("Error checking code", e);
			}
		}
		logger.debug("code does not match");
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

	@Override
	public TableSpecification modifyDefaultTableSpecification(TableSpecification spec, String table) {
		spec.setOptionalField(SECRET_FIELD, new StringFieldType(true, null, 32));
		return spec;
	}

	@Override
	public Set<String> addSuppress(Set<String> suppress) {
		suppress.add(SECRET_FIELD);
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
			if( ! verify(key, data) ) {
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
				f.addInput(KEY, "New key", new ConstantInput<>(enc,enc));
				f.addInput(NEW_CODE, "Verification code (New key)", getInput());
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
			cb.addText("This is your new 2-factor authorisation key. You will need a smart-phone app such as Google Authenticator or Microsoft Authenticator to generate the verification codes.");
			if( VERIFY_OLD_CODE.isEnabled(getContext()) && needAuth(target)) {
				cb.addText("You are changing an existing key and will have to input the codes for both the current and the new key.");
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
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.AppUserTransitionContributor#getTransitions()
	 */
	@Override
	public Map<AppUserKey, Transition<AppUser>> getTransitions(AppUserTransitionProvider prov) {
		Map<AppUserKey,Transition<AppUser>> map = new LinkedHashMap<>();
		if( enabled()) {
			
			map.put(SET_KEY,(Transition<AppUser>) new SetToptTransition(prov));
			map.put(CLEAR_KEY,new AuthorisedConfirmTransition<AppUser,AppUser>("Disable 2-factor authentication", (DirectTransition<AppUser>) new DirectClearKeyTransition(prov), prov.new ViewTransition(),REMOVE_KEY_ROLE));
		
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

}
