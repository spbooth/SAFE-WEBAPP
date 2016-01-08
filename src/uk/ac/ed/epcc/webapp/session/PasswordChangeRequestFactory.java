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
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.table.IntegerFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.filter.FilterDelete;
import uk.ac.ed.epcc.webapp.model.data.filter.SQLValueFilter;
/** Holds an one use id-string associated with a user for resetting the login password.
 * 
 * if knows the current encrypted password is also stored and the tag won't match unlesss this is unchanged
 * preventing the tag from being used once the password has been changed. 
 * 
 * @author spb
 *
 */
public class PasswordChangeRequestFactory<A extends AppUser> extends DataObjectFactory<PasswordChangeRequestFactory<A>.PasswordChangeRequest> {
	/**
	 * 
	 */
	private static final int MAX_CHECK = 512;
	static final String TAG="Tag";
	static final String USER_ID="UserID";
	static final String CHECK="Check";
	private final AppUserFactory<A> user_fac;
	public PasswordChangeRequestFactory(AppUserFactory<A> fac){
		user_fac=fac;
		setContext(fac.getContext(), "PasswordChangeRequest");
	}
	@Override
	public TableSpecification getDefaultTableSpecification(AppContext ctx,String table){
		TableSpecification spec = new TableSpecification();
		spec.setField(USER_ID, new IntegerFieldType());
		spec.setField(CHECK, new StringFieldType(false, "", MAX_CHECK));
		spec.setField(TAG, new StringFieldType(false, "", 256));
		return spec;
	}
	
	public final class PasswordChangeRequest extends DataObject{

		protected PasswordChangeRequest(Record r) {
			super(r);
		}
		public PasswordChangeRequest(AppUser user,String check) throws DataFault{
			this(res.new Record());
			record.setProperty(USER_ID, user.getID());
			record.setProperty(TAG,makeTag(user.getID(),check));
			if( check.length() > MAX_CHECK){
				check = check.substring(0, MAX_CHECK);
			}
			record.setProperty(CHECK, check);
			commit();
			
		}
	
		public String getTag(){
			return record.getStringProperty(TAG);
		}
		public A getUser(){
			return user_fac.find(record.getNumberProperty(USER_ID));
		}
		
		public String getCheck(){
			return record.getStringProperty(CHECK);
		}
	}
	public PasswordChangeRequest createRequest(A user) throws DataFault{
		assert(user != null);
		
		// delete any existing requests
		FilterDelete<PasswordChangeRequest> del = new FilterDelete<PasswordChangeRequest>(res);
		del.delete(new SQLValueFilter<PasswordChangeRequest>(getTarget(), res, USER_ID, user.getID()));
		
		PasswordAuthComposite<A> comp = user_fac.getComposite(PasswordAuthComposite.class);
		if( comp == null ){
			return null;
		}
		if ( ! comp.canResetPassword(user)){
			return null;
		}
		String check = "";
		if( comp instanceof DatabasePasswordComposite){
			check = ((DatabasePasswordComposite<A>)comp).getHandler(user).getCryptPassword();
		}
		PasswordChangeRequest req = new PasswordChangeRequest(user,check);
		req.commit();
		return req;
	}
	public PasswordChangeRequest findByTag(String tag) throws DataException{
		PasswordChangeRequest request = find(new SQLValueFilter<PasswordChangeRequestFactory<A>.PasswordChangeRequest>(getTarget(),res,TAG,tag),true );
		A user = request.getUser();
		PasswordAuthComposite<A> comp = user_fac.getComposite(PasswordAuthComposite.class);
		if( comp == null ){
			return null;
		}
		if( ! comp.canResetPassword(user)){
			return null;
		}
		String check = "";
		if( comp instanceof DatabasePasswordComposite){
			check = ((DatabasePasswordComposite<A>)comp).getHandler(user).getCryptPassword();
			if( ! check.startsWith(request.getCheck())){
				return null;
			}
		}
		
		return request;
	}
	@Override
	protected DataObject makeBDO(Record res) throws DataFault {
		return new PasswordChangeRequest(res);
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
	public AppUserFactory<A> getAppUserFactory() {
		return user_fac;
	}
	

}