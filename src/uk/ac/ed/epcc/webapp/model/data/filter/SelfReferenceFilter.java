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

	private final Class<? super T> target;
	private final IndexedReference ref;
	private final Repository res;
	private final boolean exclude;
	/** Filter that matches an {@link IndexedReference} 
	 * 
	 * @param target   factory target
	 * @param res  {@link Repository}
	 * @param ref {@link IndexedReference}
	 */
	public SelfReferenceFilter(Class<? super T> target,Repository res, IndexedReference ref){
		this(target,res,false,ref);
	}
	/** 
	 * 
	 * @param target factory target Class
	 * @param res {@link Repository}
	 * @param exclude if true, matches everything but reference
	 * @param ref {@link IndexedReference}
	 */
	public SelfReferenceFilter(Class<? super T> target,Repository res, boolean exclude ,IndexedReference ref){
		this.target=target;
		this.res=res;
		this.exclude=exclude;
		this.ref=ref;
	}
	
	public void accept(T o) {
		
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
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter#accept(uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor)
	 */
	public <X> X acceptVisitor(FilterVisitor<X, ? extends T> vis) throws Exception {
		return vis.visitPatternFilter(this);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Targetted#getTarget()
	 */
	public Class<? super T> getTarget() {
		return target;
	}
	
	
}