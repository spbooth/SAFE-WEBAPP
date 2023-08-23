//| Copyright - The University of Edinburgh 2011                            |
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
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.session;

import java.util.Calendar;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.CurrentTimeService;
import uk.ac.ed.epcc.webapp.email.Emailer;
import uk.ac.ed.epcc.webapp.email.inputs.EmailInput;
import uk.ac.ed.epcc.webapp.email.inputs.ServiceAllowedEmailFieldValidator;
import uk.ac.ed.epcc.webapp.forms.Field;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.action.FormAction;
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.MessageResult;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.table.DateFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.IntegerFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.AnonymisingFactory;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.filter.FilterDelete;
import uk.ac.ed.epcc.webapp.validation.FieldValidationSet;



public class EmailChangeRequestFactory<A extends AppUser> extends AbstractUserRequestFactory<A,EmailChangeRequestFactory<A>.EmailChangeRequest> implements AnonymisingFactory{
	/**
	 * 
	 */
	public static final String REQUEST_ACTION = "Request";
	public static final String VERIFY_ACTION = "Verify";
	static final String NEW_EMAIL="NewEmail";
	
	
	
	public EmailChangeRequestFactory(AppUserFactory fac){
		super(fac);
		setContext(fac.getContext(), "EmailChangeRequest");
	}
	public EmailChangeRequestFactory(AppContext conn) {
		this(conn.getService(SessionService.class).getLoginFactory());
	}
	@Override
	public TableSpecification getDefaultTableSpecification(AppContext ctx,String table){
		TableSpecification spec = new TableSpecification();
		spec.setField(USER_ID, new IntegerFieldType());
		spec.setField(NEW_EMAIL, new StringFieldType(false, "", 64));
		spec.setField(TAG, new StringFieldType(false, "", 256));
		spec.setField(EXPIRES, new DateFieldType(true, null));
		return spec;
	}
	
	public final class EmailChangeRequest extends uk.ac.ed.epcc.webapp.session.AbstractUserRequestFactory.AbstractRequest<A>{

		protected EmailChangeRequest(Record r) {
			super(getAppUserFactory(),r);
		}
		public EmailChangeRequest(AppUser user, String new_email) throws DataFault{
			this(res.new Record());
			record.setProperty(USER_ID, user.getID());
			record.setProperty(NEW_EMAIL, new_email);
			record.setProperty(TAG,makeTag(user.getID(),new_email));
			CurrentTimeService time = getContext().getService(CurrentTimeService.class);
			Calendar c = Calendar.getInstance();
			c.setTime(time.getCurrentTime());
			c.add(Calendar.DAY_OF_YEAR, getContext().getIntegerParameter("email_change.max_valid_days", 10));
			record.setOptionalProperty(EXPIRES, c.getTime());
			commit();
			
		}
		public void complete() throws DataException{
			AppUser user = getUser();
			AppUserNameFinder finder = user_fac.getRealmFinder(EmailNameFinder.EMAIL);
			finder.setName(user, record.getStringProperty(NEW_EMAIL));
			finder.verified(user); // email address verified
			user.commit();
			delete();
		}
		
		public String getEmail() {
			return record.getStringProperty(NEW_EMAIL);
		}
	}
	public EmailChangeRequest createRequest(AppUser user, String email) throws DataFault{
		assert(user != null);
		assert( email != null);
		EmailChangeRequest req = new EmailChangeRequest(user,email);
		req.commit();
		return req;
	}
	
	@Override
	protected EmailChangeRequestFactory<A>.EmailChangeRequest makeBDO(Record res) throws DataFault {
		return new EmailChangeRequest(res);
	}
	

	
	public class RequestAction extends FormAction{
        private AppUser user;
        public RequestAction(AppUser user){
        	this.user=user;
        }
		@Override
		public FormResult action(Form f) throws ActionException {
			try {
				
				EmailChangeRequest req = createRequest(user,(String)f.get(EmailNameFinder.EMAIL));
				Emailer em= Emailer.getFactory(getContext());
				em.newEmailRequest(user, req);
				return new MessageResult("email_change_request_made");
			} catch (Exception e) {
				getLogger().error("Error making EmailChangeRequest",e);
				throw new ActionException("Internal Error");
			}
		}
		@Override
		public String getHelp() {
			return "Request email be updated to a new address";
		}
		
	}
	public class VerifyAction extends FormAction{
        private AppUser user;
        public VerifyAction(AppUser user){
        	this.user=user;
        	setMustValidate(false);
        }
		@Override
		public FormResult action(Form f) throws ActionException {
			try {
				
				EmailChangeRequest req = createRequest(user,user.getEmail());
				Emailer em= Emailer.getFactory(getContext());
				em.newEmailRequest(user, req);
				return new MessageResult("email_verify_request_made",user.getEmail());
			} catch (Exception e) {
				getLogger().error("Error making EmailChangeRequest",e);
				throw new ActionException("Internal Error");
			}
		}
		@Override
		public String getHelp() {
			return "Re-validate your current email address without changing it";
		}
		
	}
	
	 @SuppressWarnings("unchecked")
	public  void makeRequestForm(AppUser user,Form f, boolean include_verify){
	    	// use email field so we can use the normal field input
		    // the factory class may have a special version
	    	AppUserFactory factory = getAppUserFactory();
	    	EmailInput input = new EmailInput();
	    	
			f.addInput(EmailNameFinder.EMAIL, "New Email Address", input);
			// Must not change to existing email unless already taken by same user.
			Field field = f.getField(EmailNameFinder.EMAIL);
			field.addValidator(EmailNameFinder.getEmailValidator(getContext()));
			field.removeValidator(new ParseFactoryValidator<AppUser>(factory, null));
			field.addValidator(new ParseFactoryValidator<AppUser>(factory, user));
			
	    	f.addAction(REQUEST_ACTION, new RequestAction(user));
	    	String email = null;
	    	if( user != null ) {
	    		email=user.getEmail();
	    	}
	    	if( include_verify && email != null && ! email.isEmpty()) {
	    		f.addAction(VERIFY_ACTION, new VerifyAction(user));
	    	}
	    }
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.AnonymisingFactory#anonymise()
	 */
	@Override
	public void anonymise() throws DataFault {
		FilterDelete del = new FilterDelete<>(res);
		del.delete(null);
		
	}
	
	
}