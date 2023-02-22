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
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;

/** A {@link AbstractRequestFactory} for requests tied to a specific target object.
 * @author Stephen Booth
 *
 * @param <A>
 */
public abstract class AbstractTargetRequestFactory<A extends DataObject,R extends AbstractTargetRequestFactory.AbstractRequest<A>>
		extends AbstractRequestFactory<R> {

	/**
	 * @param fac
	 */
	protected AbstractTargetRequestFactory(DataObjectFactory<A> fac) {
		super();
		this.fac = fac;
	}

	protected static final String Target_ID = "TargetID";
	protected final DataObjectFactory<A> fac;
	
	@Override
	protected TableSpecification getDefaultTableSpecification(AppContext c, String table) {
		TableSpecification spec = super.getDefaultTableSpecification(c, table);
		spec.setField(Target_ID, new IntegerFieldType());
		
		return spec;
	}
	public final DataObjectFactory<A> getFactory() {
		return fac;
	}
	
	public static class AbstractRequest<A extends DataObject> extends AbstractRequestFactory.AbstractRequest{
		private final DataObjectFactory<A> fac;
		/**
		 * @param r
		 */
		protected AbstractRequest(DataObjectFactory<A> fac,Record r) {
			super(r);
			this.fac=fac;
		}
		public final A getTarget(){
			return getFactory().find(record.getNumberProperty(Target_ID));
		}
		public final DataObjectFactory<A> getFactory(){
			return fac;
		}
		
		
	}

}