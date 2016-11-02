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
package uk.ac.ed.epcc.webapp.model.data;

import uk.ac.ed.epcc.webapp.jdbc.expr.CannotFilterException;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedReference;
/** Field value for the current record
 * 
 * @author spb
 *
 * @param <T>
 */


public class SelfSQLValue<T extends DataObject> extends AbstractSelfSQLValue<T, T> {
	public SelfSQLValue(DataObjectFactory<T> fac) {
		super(fac);
	}
	
	@Override
	public String toString(){
		return "SelfId";
	}
	
	
	public IndexedReference<T> getValue(T r) {
		Record r1 = r.record;
		if( r1.hasID()){
			// again if not set we use a zero id value
			return makeReference(r1.getID());
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.FilterProvider#getFilterType()
	 */
	public Class<? super T> getFilterType() {
		return getFactory().getTarget();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.IndexedSQLValue#getSQLFilter(uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter)
	 */
	@Override
	public SQLFilter<T> getSQLFilter(SQLFilter<T> fil) throws CannotFilterException {
		return fil;
	}

	
	
	

}