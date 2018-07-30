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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.email.Emailer;
import uk.ac.ed.epcc.webapp.email.inputs.EmailInput;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.action.FormAction;
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.MessageResult;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.table.IntegerFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.AnonymisingFactory;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.filter.FilterDelete;
import uk.ac.ed.epcc.webapp.model.data.filter.SQLValueFilter;



public class EmailChangeRequestFactory extends DataObjectFactory<EmailChangeRequestFactory.EmailChangeRequest> implements AnonymisingFactory{
	/**
	 * 
	 */
	public static final String REQUEST_ACTION = "Request";
	static final String NEW_EMAIL="NewEmail";
	static final String TAG="Tag";
	static final String USER_ID="UserID";
	private final AppUserFactory user_fac;
	public EmailChangeRequestFactory(AppUserFactory fac){
		user_fac=fac;
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
		return spec;
	}
	
	public final class EmailChangeRequest extends DataObject{

		protected EmailChangeRequest(Record r) {
			super(r);
		}
		public EmailChangeRequest(AppUser user, String new_email) throws DataFault{
			this(res.new Record());
			record.setProperty(USER_ID, user.getID());
			record.setProperty(NEW_EMAIL, new_email);
			record.setProperty(TAG,makeTag(user.getID(),new_email));
			commit();
			
		}
		public void complete() throws DataException{
			AppUser user = (AppUser) user_fac.find(record.getIntProperty(USER_ID));
			user.setEmail(record.getStringProperty(NEW_EMAIL));
			user.commit();
			delete();
		}
		public String getEmail() {
			return record.getStringProperty(NEW_EMAIL);
		}
		public String getTag(){
			return record.getStringProperty(TAG);
		}
		
	}
	public EmailChangeRequest createRequest(AppUser user, String email) throws DataFault{
		assert(user != null);
		assert( email != null);
		EmailChangeRequest req = new EmailChangeRequest(user,email);
		req.commit();
		return req;
	}
	public EmailChangeRequest findByTag(String tag) throws DataException{
		return find(new SQLValueFilter<EmailChangeRequest>(getTarget(),res,TAG,tag),true );
	}
	@Override
	protected DataObject makeBDO(Record res) throws DataFault {
		return new EmailChangeRequest(res);
	}
	public String makeTag(int id,String seed) {
		Logger log = getContext().getService(LoggerService.class).getLogger(getClass());
		StringBuilder input = new StringBuilder();
		input.append(seed);
		RandomService serv = getContext().getService(RandomService.class);
		input.append(serv.randomString(64));
		
		log.debug("Input is "+input.toString());
		try {
			// obfuscate the tag
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(input.toString().getBytes());
			StringBuilder output= new StringBuilder();
			output.append(id); // make sure different users can't have a tag clash.
			output.append("-");
			byte tag[]=digest.digest();
			for(int i=0;i<tag.length;i++){
				output.append(Integer.toString(tag[i]-Byte.MIN_VALUE, 36));
			}
			log.debug("output is"+output.toString());
			return output.toString();
		} catch (NoSuchAlgorithmException e) {
			getContext().error(e,"Error making digest");
			return input.toString();
		}
	}
	public AppUserFactory getAppUserFactory() {
		return user_fac;
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
				Emailer em= new Emailer(getContext());
				em.newEmailRequest(user, req);
				return new MessageResult("email_change_request_made");
			} catch (Exception e) {
				getContext().error(e,"Error making EmailChangeRequest");
				throw new ActionException("Internal Error");
			}
		}
		
	}
	
	 @SuppressWarnings("unchecked")
	public  void makeRequestForm(AppUser user,Form f){
	    	// use email field so we can use the normal field input
		    // the factory class may have a special version
	    	AppUserFactory factory = getAppUserFactory();
			Input input = (Input) factory.getSelectors().get(EmailNameFinder.EMAIL);
	    	if( input == null ){
	    		input = new EmailInput();
	    	}
			f.addInput(EmailNameFinder.EMAIL, "New Email Address", input);
			// Must not change to existing email unless already taken by same user.
			f.getField(EmailNameFinder.EMAIL).removeValidator(new ParseFactoryValidator<AppUser>(factory, null));
			f.getField(EmailNameFinder.EMAIL).addValidator(new ParseFactoryValidator<AppUser>(factory, user));
			
	    	f.addAction(REQUEST_ACTION, new RequestAction(user));
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