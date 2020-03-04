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

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.jdbc.table.IntegerFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;

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
		spec.setField(USER_ID, new IntegerFieldType());
		
		return spec;
	}
	public final AppUserFactory<A> getAppUserFactory() {
		return user_fac;
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