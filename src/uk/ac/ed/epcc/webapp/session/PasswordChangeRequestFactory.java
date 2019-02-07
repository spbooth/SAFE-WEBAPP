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
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.filter.FilterDelete;
import uk.ac.ed.epcc.webapp.model.data.filter.SQLValueFilter;
/** Holds an one use id-string associated with a user for resetting the login password.
 * 
 * if known the current encrypted password is also stored and the tag won't match unlesss this is unchanged
 * preventing the tag from being used once the password has been changed. 
 * 
 * @author spb
 *
 */
public class PasswordChangeRequestFactory<A extends AppUser> extends AbstractRequestFactory<A,PasswordChangeRequestFactory<A>.PasswordChangeRequest> {
	/**
	 * 
	 */
	private static final int MAX_CHECK = 512;
	
	static final String CHECK="Check";
	
	public PasswordChangeRequestFactory(AppUserFactory<A> fac){
		super(fac);
		setContext(fac.getContext(), "PasswordChangeRequest");
	}
	@Override
	public TableSpecification getDefaultTableSpecification(AppContext ctx,String table){
		TableSpecification spec = super.getDefaultTableSpecification(ctx, table);
		
		spec.setField(CHECK, new StringFieldType(false, "", MAX_CHECK));
		
		
		return spec;
	}
	
	public final class PasswordChangeRequest extends AbstractRequestFactory.AbstractRequest{

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
			CurrentTimeService time = getContext().getService(CurrentTimeService.class);
			Calendar c = Calendar.getInstance();
			c.setTime(time.getCurrentTime());
			c.add(Calendar.DAY_OF_YEAR, getContext().getIntegerParameter("password_change.max_valid_days", 10));
			record.setOptionalProperty(EXPIRES, c.getTime());
			commit();
			
		}
	
		
		public String getCheck(){
			return record.getStringProperty(CHECK);
		}

		
	}
	public PasswordChangeRequest createRequest(A user) throws DataFault{
		assert(user != null);
		
		// delete any existing requests
		FilterDelete<PasswordChangeRequest> del = new FilterDelete<>(res);
		del.delete(new SQLValueFilter<>(getTarget(), res, USER_ID, user.getID()));
		
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
		PasswordChangeRequest request = find(new SQLValueFilter<>(getTarget(),res,TAG,tag),true );
		if( request == null ){
			return null;
		}
		A user = (A) request.getUser();
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
	
}