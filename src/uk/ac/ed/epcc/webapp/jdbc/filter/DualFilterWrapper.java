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
package uk.ac.ed.epcc.webapp.jdbc.filter;

import java.util.List;

/** A wrapper to combine a {@link PatternFilter} and an {@link AcceptFilter}
 * into a {@link DualFilter}.
 * 
 * The two filters <em>MUST</em> implement the same selection.
 * @author spb
 *
 */
public class DualFilterWrapper<T> implements DualFilter<T> {

	/**
	 * @param sql
	 * @param accept
	 */
	public DualFilterWrapper(PatternFilter<T> sql, AcceptFilter<T> accept) {
		super();
		this.sql = sql;
		this.accept = accept;
	}

	private final PatternFilter<T> sql;
	private final AcceptFilter<T> accept;
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.PatternFilter#getParameters(java.util.List)
	 */
	@Override
	public List<PatternArgument> getParameters(List<PatternArgument> list) {
		return sql.getParameters(list);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.PatternFilter#addPattern(java.lang.StringBuilder, boolean)
	 */
	@Override
	public StringBuilder addPattern(StringBuilder sb, boolean qualify) {
		return sql.addPattern(sb, qualify);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter#acceptVisitor(uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor)
	 */
	@Override
	public <X> X acceptVisitor(FilterVisitor<X, ? extends T> vis) throws Exception {
		return vis.visitDualFilter(this);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Targetted#getTarget()
	 */
	@Override
	public Class<? super T> getTarget() {
		return sql.getTarget();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.AcceptFilter#accept(java.lang.Object)
	 */
	@Override
	public boolean accept(T o) {
		return accept.accept(o);
	}

}
