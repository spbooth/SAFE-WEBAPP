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

import java.util.*;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.content.PreDefinedContent;
import uk.ac.ed.epcc.webapp.email.Emailer;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.action.FormAction;
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.inputs.IntegerInput;
import uk.ac.ed.epcc.webapp.forms.result.ChainedTransitionResult;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.MessageResult;
import uk.ac.ed.epcc.webapp.forms.transition.*;
import uk.ac.ed.epcc.webapp.jdbc.table.IntegerFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.AnonymisingComposite;
import uk.ac.ed.epcc.webapp.model.SummaryContributer;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.session.*;

/**  A {@link FormAuthComposite} that implements time-based authentication codes for application login compatible
 * with google authenticator
 * <ul>
 * <li>https://tools.ietf.org/html/rfc4226</li>
 * <li>https://tools.ietf.org/html/rfc6238</li>
 * </ul>
 * 
 * This class adds support for making the use of TOTP mandatory and a recovery code
 * 
 * @author Stephen Booth
 *
 */
public class TotpCodeAuthComposite<A extends AppUser> extends AbstractTotpCodeAuthComposite<A,FormAuthComposite> implements AppUserTransitionContributor, SummaryContributer<A>, RequiredPageProvider<A>, AnonymisingComposite<A> {
	
	public static final Feature REQUIRED_TWO_FACTOR= new Feature("two_factor.required",false,"Is two factor authentication required");
	protected static final int MAX_RECOVERY_CODE = 99999999;
	
	public static final String MAKE_RECOVERY_CODE_ROLE="MakeRecoveryCode";
	public static final String REMOVE_KEY_ROLE="RemoveTwoFactor";
	public static final String RECOVERY_FIELD="AuthCodeRecover";
	/**
	 * @param fac
	 */
	public TotpCodeAuthComposite(AppUserFactory<A> fac,String tag) {
		super(fac,tag);
		
	}

	


	
	
	

	/**
	 * @param user
	 */
	public void clearSecret(A user) {
		super.clearSecret(user);
		Record record = getRecord(user);
		record.setOptionalProperty(RECOVERY_FIELD, null);
	}
	
   
	
	public int getRecoveryCode(A user) {
		return getRecord(user).getIntProperty(RECOVERY_FIELD, -1);
	}
	public void setRecoveryCode(A user, int code) {
		getRecord(user).setProperty(RECOVERY_FIELD, code);
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

	
	
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.twofactor.CodeAuthComposite#verify(java.lang.Object)
	 */
	@Override
	public boolean verify(A user,Integer value,StringBuilder notes) {
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
			return super.verify(user, value,notes);
		} catch (Exception e) {
			getLogger().error("Error getting secret", e);
			return false;
		}
	}
	
	public boolean useRecoveryCode() {
		return getRepository().hasField(RECOVERY_FIELD);
	}
	@Override
	public TableSpecification modifyDefaultTableSpecification(TableSpecification spec, String table) {
		super.modifyDefaultTableSpecification(spec, table);
		spec.setOptionalField(RECOVERY_FIELD, new IntegerFieldType(true, null));
		return spec;
	}

	@Override
	public Set<String> addSuppress(Set<String> suppress) {
		super.addSuppress(suppress);
		suppress.add(RECOVERY_FIELD);
		return suppress;
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
	
	public class RecoveryCodeTransition extends AbstractFormTransition<A> implements ExtraContent<A>{
		

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

	
	
}
