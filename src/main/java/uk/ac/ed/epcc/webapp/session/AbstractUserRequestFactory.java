//| Copyright - The University of Edinburgh 2019                            |
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
package uk.ac.ed.epcc.webapp.session;

import java.util.List;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.jdbc.table.IntegerFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.data.FilterResult;
import uk.ac.ed.epcc.webapp.model.data.NullFilterResult;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;

/** A {@link AbstractRequestFactory} for requests tied to a specific {@link AppUser}
 * @author Stephen Booth
 *
 * @param <A>
 */
public abstract class AbstractUserRequestFactory<A extends AppUser,R extends AbstractUserRequestFactory.AbstractRequest<A>>
		extends AbstractRequestFactory<R> {

	/**
	 * @param user_fac
	 */
	protected AbstractUserRequestFactory(AppUserFactory<A> user_fac) {
		super();
		this.user_fac = user_fac;
	}

	protected static final String USER_ID = "UserID";
	protected final AppUserFactory<A> user_fac;
	
	@Override
	protected TableSpecification getDefaultTableSpecification(AppContext c, String table) {
		TableSpecification spec = super.getDefaultTableSpecification(c, table);
		spec.setCurrentTag("AbstractUserRequestFactory");
		spec.setField(USER_ID, new IntegerFieldType());
		spec.clearCurrentTag();
		return spec;
	}
	public final AppUserFactory<A> getAppUserFactory() {
		return user_fac;
	}
	
	/**
	 * Finds any existing unassociated requests that could be for the given user, and associate them.
	 * By default, this does not match or update anything, but subclasses can override this to match by an appropriate method, e.g. email
	 * 
	 * @param user
	 * @throws DataFault
	 */
	public void linkNewUser(A user) throws DataFault {
		return;
	}
	
	public static class AbstractRequest<A extends AppUser> extends AbstractRequestFactory.AbstractRequest{
		private final AppUserFactory<A> user_fac;
		/**
		 * @param r
		 */
		protected AbstractRequest(AppUserFactory<A> user_fac,Record r) {
			super(r);
			this.user_fac=user_fac;
		}
		public final A getUser(){
			return getUserFactory().find(record.getNumberProperty(USER_ID));
		}
		public final AppUserFactory<A> getUserFactory(){
			return user_fac;
		}
		
		
	}

}