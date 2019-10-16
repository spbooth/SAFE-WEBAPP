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
package uk.ac.ed.epcc.webapp.jdbc.expr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.ConstPatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.GenericBinaryFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.MatchCondition;
import uk.ac.ed.epcc.webapp.jdbc.filter.NoSQLFilterException;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.model.data.Repository;


/** A constant value {@link SQLAccessor}
 * 
 * @author spb
 *
 * @param <T>  type of constant
 * @param <R> type of target object
 */
public final class ConstExpression<T,R> implements SQLExpression<T>, SQLAccessor<T,R>, GroupingSQLValue<T>, FilterProvider<R, T> {
	  	private final T n;
	    private final Class<T> target;
	    private final Class<R> filter_type;
	    private final boolean log;
	    public ConstExpression(Class<T>target, T n) {
	    	this((Class<R>) Object.class,target,n);
	    }
	    public ConstExpression(Class<R> filter_type,Class<T>target,T n){
	    	this(filter_type,target,n,true);
	    }
	    public ConstExpression(Class<R> filter_type,Class<T>target,T n,boolean log){
	    	this.n=n;
	    	this.filter_type=filter_type;
	    	this.target=target;
	    	this.log=log;
	    }
		@Override
		public int add(StringBuilder sb, boolean qualify) {
			sb.append("?");
			return 1;
		}
		@Override
		public List<PatternArgument> getParameters(List<PatternArgument> list) {
			list.add(new ConstPatternArgument<>(target, n,log));
			return list;
		}
		
		@Override
		public T makeObject(ResultSet rs, int pos) throws DataException, SQLException  {
			
			// Note that this function also processes function results like sum(1)
			// so it has to process the result set not just return the constant.
			return Repository.makeTargetObject(target, rs, pos);
			
		}
		@Override
		public Class<T> getTarget() {
			return target;
		}
		@Override
		public String toString() {
			return "Const("+n.toString()+")";
		}
		@Override
		public SQLFilter getRequiredFilter() {
			return null;
		}
		@Override
		public T getValue(R r) {
			return n;
		}
		
		public T getValue(){
			return n;
		}
		@Override
		public boolean canSet() {
			
			return false;
		}
		@Override
		public void setValue(R r, T value) {
			throw new UnsupportedOperationException("Set not supported");
			
		}
		@Override
		public int hashCode() {
			return n.hashCode();
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ConstExpression other = (ConstExpression) obj;
			if (n == null) {
				if (other.n != null)
					return false;
			} else if (!n.equals(other.n))
				return false;
			if (target == null) {
				if (other.target != null)
					return false;
			} else if (target != other.target)
				return false;
			return true;
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.expr.GroupingSQLValue#addGroup(java.lang.StringBuilder, boolean)
		 */
		@Override
		public int addGroup(StringBuilder sb, boolean qualify) {
			// constant does not affect group-by
			return 0;
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.expr.GroupingSQLValue#getGroupParameters(java.util.List)
		 */
		@Override
		public List<PatternArgument> getGroupParameters(
				List<PatternArgument> list) {
			return list;
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.expr.FilterProvider#getFilter(uk.ac.ed.epcc.webapp.jdbc.filter.MatchCondition, java.lang.Object)
		 */
		@Override
		public SQLFilter<R> getFilter(MatchCondition match, T val) throws CannotFilterException, NoSQLFilterException {
			return new GenericBinaryFilter<R>(getFilterType(), match.compare(n,val));
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.expr.FilterProvider#getNullFilter(boolean)
		 */
		@Override
		public SQLFilter<R> getNullFilter(boolean is_null) throws CannotFilterException, NoSQLFilterException {
			return new GenericBinaryFilter<R>(getFilterType(), (n == null) == is_null);
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.expr.FilterProvider#getOrderFilter(boolean)
		 */
		@Override
		public SQLFilter<R> getOrderFilter(boolean descending) throws CannotFilterException, NoSQLFilterException {
			// No impact on order
			return null;
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.expr.FilterProvider#getFilterType()
		 */
		@Override
		public Class<R> getFilterType() {
			return filter_type;
		}
}