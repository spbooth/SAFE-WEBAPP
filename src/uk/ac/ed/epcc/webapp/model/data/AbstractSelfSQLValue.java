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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.expr.CannotFilterException;
import uk.ac.ed.epcc.webapp.jdbc.expr.FilterProvider;
import uk.ac.ed.epcc.webapp.jdbc.expr.IndexedSQLValue;
import uk.ac.ed.epcc.webapp.jdbc.expr.SQLAccessor;
import uk.ac.ed.epcc.webapp.jdbc.filter.GenericBinaryFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.MatchCondition;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.filter.PrimaryOrderFilter;
import uk.ac.ed.epcc.webapp.model.data.filter.SelfReferenceFilter;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedReference;
/** Field value for a self reference
 * It targets the primary key of the factory. The filter type may be different
 * but the repository is assumed to be added to the query source.
 * 
 * 
 * @author spb
 * @see SelfSQLValue
 * @see TupleSelfSQLValue
 *
 * @param <T> type of reference/repository
 * @param <R> type of filter
 */


public abstract class AbstractSelfSQLValue<T extends DataObject,R> implements SQLAccessor<IndexedReference<T>,R>,FilterProvider<R, IndexedReference<T>>,IndexedSQLValue<R, T> {
	private final DataObjectFactory<T> fac;
	public AbstractSelfSQLValue(DataObjectFactory<T> fac) {
		this.fac=fac;
	}
	
	
	public final int add(StringBuilder sb, boolean qualify) {
		fac.res.addUniqueName(sb, qualify, true);
		return 1;
	}
	public final List<PatternArgument> getParameters(List<PatternArgument> list) {
		return list;
	}
	
	public final IndexedReference<T> makeObject(ResultSet rs, int pos)
			throws DataException {
		try {
			int id=rs.getInt(pos);
			IndexedReference<T> res =  makeReference(id);
			return res;
		} catch (SQLException e) {
			  throw new DataFault("Error making IndexedReferencefield result",e);
		}
	}
	public final IndexedReference<T> makeReference(int id) {
		return fac.makeReference(id);
	}
	
	
	public final Class<IndexedReference> getTarget() {
		return IndexedReference.class;
	}
	@Override
	public String toString(){
		return "SelfId";
	}
	
	
	public final SQLFilter getRequiredFilter() {
		return null;
	}
	
	public final boolean canSet() {
		return false;
	}

	public final void setValue(R r, IndexedReference<T> value) {
		throw new ConsistencyError("Cannot set self reference");
	}

	public final SQLFilter<R> getFilter(MatchCondition match, IndexedReference<T> val)
			throws CannotFilterException {
		if( match == null ){
			return new SelfReferenceFilter<R>(getFilterType(),fac.res,val);
		}else if( match == MatchCondition.NE){
			return new SelfReferenceFilter<R>(getFilterType(),fac.res,true,val);
		}
		throw new CannotFilterException("Relative MatchCondition requested for IndexedReference");
	}

	public final SQLFilter<R> getNullFilter(boolean is_null)
			throws CannotFilterException {
		return new GenericBinaryFilter<>(getFilterType(), ! is_null);
	}
	public final SQLFilter<R> getOrderFilter(boolean descending)
			throws CannotFilterException {
		return new PrimaryOrderFilter<R>(getFilterType(),fac.res, descending);
	}


	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.expr.IndexedSQLValue#getFactory()
	 */
	@Override
	public final DataObjectFactory<T> getFactory(){
		return fac;
	}

	
	

}