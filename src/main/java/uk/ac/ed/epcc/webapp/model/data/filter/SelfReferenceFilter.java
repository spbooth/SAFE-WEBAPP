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
package uk.ac.ed.epcc.webapp.model.data.filter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternArgument;
import uk.ac.ed.epcc.webapp.jdbc.filter.PatternFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.model.data.Repository;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedReference;
/** Filter to select by IndexedReference
 * 
 * It selects on the primary key of the provided repository using a reference to the corresponding
 * data object.
 * 
 * The type of the filter is specified separately as this might be a composite object made from a join
 * 
 * @author spb
 * @see SQLIdFilter
 * @param <T> type of filter
 */


public class SelfReferenceFilter<T> implements SQLFilter<T> , PatternFilter<T>{

	private final Class<T> target;
	private final IndexedReference ref;
	private final Repository res;
	private final boolean exclude;
	/** Filter that matches an {@link IndexedReference} 
	 * 
	 * @param target   factory target
	 * @param res  {@link Repository}
	 * @param ref {@link IndexedReference}
	 */
	public SelfReferenceFilter(Class<T> target,Repository res, IndexedReference ref){
		this(target,res,false,ref);
	}
	/** 
	 * 
	 * @param target factory target Class
	 * @param res {@link Repository}
	 * @param exclude if true, matches everything but reference
	 * @param ref {@link IndexedReference}
	 */
	public SelfReferenceFilter(Class<T> target,Repository res, boolean exclude ,IndexedReference ref){
		this.target=target;
		this.res=res;
		this.exclude=exclude;
		this.ref=ref;
	}
	

	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		list.add(new PatternArgument() {
			
			public String getField() {
				return res.addUniqueName(new StringBuilder(), false, false).toString();
			}
			
			public Object getArg() {
				return ref;
			}
			
			public boolean canLog() {
				return true;
			}
			
			public void addArg(PreparedStatement stmt, int pos) throws SQLException {
				stmt.setInt(pos, ref.getID());	
			}
		});
		return list;
	}

	public StringBuilder addPattern(Set<Repository> tables,StringBuilder sb,boolean qualify) {
		res.addUniqueName(sb, qualify, true);
		if( exclude ){
			sb.append(" != ?");
		}else{
			sb.append("=?");
		}
		return sb;
	}



	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Targetted#getTarget()
	 */
	public Class<T> getTarget() {
		return target;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (exclude ? 1231 : 1237);
		result = prime * result + ((ref == null) ? 0 : ref.hashCode());
		result = prime * result + ((res == null) ? 0 : res.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SelfReferenceFilter other = (SelfReferenceFilter) obj;
		if (exclude != other.exclude)
			return false;
		if (ref == null) {
			if (other.ref != null)
				return false;
		} else if (!ref.equals(other.ref))
			return false;
		if (res == null) {
			if (other.res != null)
				return false;
		} else if (!res.equals(other.res))
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		return true;
	}
	
	
}