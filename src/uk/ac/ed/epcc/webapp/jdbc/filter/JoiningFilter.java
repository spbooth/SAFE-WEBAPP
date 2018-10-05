//| Copyright - The University of Edinburgh 2018                            |
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

import uk.ac.ed.epcc.webapp.model.data.DataObject;

/** A {@link SQLFilter} that adds a selection based on a filter on a linked table.
 * @author Stephen Booth
 *
 */
public class JoiningFilter<T extends DataObject, R extends DataObject> implements SQLFilter<T>{

	/**
	 * @param link_clause
	 * @param remote_filter
	 */
	public JoiningFilter(Class<? super T> target,JoinClause<T, R> link_clause, SQLFilter<R> remote_filter) {
		super();
		this.target=target;
		this.link_clause = link_clause;
		this.remote_filter = remote_filter;
	}

	private final Class<? super T> target;
	private final JoinClause<T, R> link_clause;
	private final SQLFilter<R> remote_filter;
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter#acceptVisitor(uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor)
	 */
	@Override
	public <X> X acceptVisitor(FilterVisitor<X, ? extends T> vis) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Targetted#getTarget()
	 */
	@Override
	public Class<? super T> getTarget() {
		return target;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter#accept(java.lang.Object)
	 */
	@Override
	public void accept(T o) {
		
	}

}
