//| Copyright - The University of Edinburgh 2016                            |
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
import uk.ac.ed.epcc.webapp.jdbc.filter.FilterConverter;
import uk.ac.ed.epcc.webapp.jdbc.filter.MatchCondition;
import uk.ac.ed.epcc.webapp.jdbc.filter.NoSQLFilterException;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.model.data.TupleFactory.Tuple;
import uk.ac.ed.epcc.webapp.model.data.TupleFactory.TupleAndFilter;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedReference;

/**
 * @author spb
 *
 */
public class TupleSelfSQLValue<A extends DataObject,T extends Tuple<A>> extends AbstractSelfSQLValue<A,T> {

	private final TupleFactory<A, DataObjectFactory<A>, Tuple<A>> tuple_fac;
	/**
	 * @param fac
	 */
	public TupleSelfSQLValue(TupleFactory<A, DataObjectFactory<A>, Tuple<A>> tuple_fac,DataObjectFactory fac) {
		super(fac);
		this.tuple_fac=tuple_fac;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.Accessor#getValue(java.lang.Object)
	 */
	@Override
	public IndexedReference<A> getValue(T r) {
		return getFactory().makeReference(r.get(getFactory().getTag()));
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.FilterProvider#getFilterType()
	 */
	@Override
	public Class<? super T> getFilterType() {
		return tuple_fac.getTarget();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.IndexedSQLValue#getSQLFilter(uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter)
	 */
	@Override
	public SQLFilter<T> getSQLFilter(SQLFilter<A> fil) throws CannotFilterException {
		if( fil == null ){
			return null;
		}
		try{
			TupleAndFilter and = tuple_fac.new TupleAndFilter();
			and.addMemberFilter(getFactory().getTag(), fil);
			return FilterConverter.convert(and);
		}catch(Exception e){
			throw new CannotFilterException(e);
		}
	}

}
