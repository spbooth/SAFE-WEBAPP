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
import java.util.List;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.ConstPatternArgument;
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
public final class ConstExpression<T,R> implements SQLExpression<T>, SQLAccessor<T,R>, GroupingSQLValue<T> {
	  	private final T n;
	    private final Class<T> target;
	    private final boolean log;
	    public ConstExpression(Class<T>target,T n){
	    	this(target,n,true);
	    }
	    public ConstExpression(Class<T>target,T n,boolean log){
	    	this.n=n;
	    	this.target=target;
	    	this.log=log;
	    }
		public int add(StringBuilder sb, boolean qualify) {
			sb.append("?");
			return 1;
		}
		public List<PatternArgument> getParameters(List<PatternArgument> list) {
			list.add(new ConstPatternArgument<T>(target, n,log));
			return list;
		}
		
		public T makeObject(ResultSet rs, int pos) throws DataException  {
			
			// Note that this function also processes function results like sum(1)
			// so it has to process the result set not just return the constant.
			return Repository.makeTargetObject(target, rs, pos);
			
		}
		public Class<T> getTarget() {
			return target;
		}
		@Override
		public String toString() {
			return "Const("+n.toString()+")";
		}
		public SQLFilter getRequiredFilter() {
			return null;
		}
		public T getValue(R r) {
			return n;
		}
		
		public T getValue(){
			return n;
		}
		public boolean canSet() {
			
			return false;
		}
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
		public int addGroup(StringBuilder sb, boolean qualify) {
			// constant does not affect group-by
			return 0;
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.expr.GroupingSQLValue#getGroupParameters(java.util.List)
		 */
		public List<PatternArgument> getGroupParameters(
				List<PatternArgument> list) {
			return list;
		}
}