//| Copyright - The University of Edinburgh 2013                            |
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
package uk.ac.ed.epcc.webapp.jdbc.filter;

import java.util.List;
import java.util.Set;

import uk.ac.ed.epcc.webapp.model.data.Repository;


/** Invert the sense of a {@link SQLFilter}
 * @author spb
 * @param <Q>  target type of this filter
 * @param <F>  full type of nested filter
 *
 */

public class SQLNotFilter<Q,F extends PatternFilter<Q>&SQLFilter<Q>> implements SQLFilter<Q>,PatternFilter<Q>,NegatingFilter<F>{
	//TODO convert this to use the {@link SQLFilterVisitor}
	private final F nested;
	
	protected  SQLNotFilter(F fil){
		this.nested= fil;
	}
	
	public F getNested(){
		return nested;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.BaseSQLFilter#accept(uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilterVisitor)
	 */
	public <X> X acceptVisitor(FilterVisitor<X,Q> vis) throws Exception {
		return vis.visitPatternFilter(this);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter#accept(java.lang.Object)
	 */
	public void accept(Q o) {
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.PatternFilter#getParameters(java.util.List)
	 */
	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		return nested.getParameters(list);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.PatternFilter#addPattern(java.lang.StringBuilder, boolean)
	 */
	public StringBuilder addPattern(Set<Repository> tables,StringBuilder sb, boolean qualify) {
		sb.append("NOT(");
		nested.addPattern(tables,sb, qualify);
		sb.append(")");
		return sb;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Targetted#getTarget()
	 */
	public Class<Q> getTarget() {
		return nested.getTarget();
	}
	public String toString() {
		return "SQLNotFilter("+nested+")";
	}

}