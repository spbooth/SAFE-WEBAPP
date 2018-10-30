//| Copyright - The University of Edinburgh 2017                            |
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

/** A wrapper that converts any {@link BaseFilter} into an {@link AcceptFilter}
 * using a {@link FilterMatcher}. 
 * 
 * Use with caution as this might not be the best implementation of a composite filter
 * @see ConvertPureAcceptFilterVisitor
 * @author spb
 * @param <T> type of filter
 *
 */
public class ConvertToAcceptFilter<T> implements AcceptFilter<T> {

	public ConvertToAcceptFilter(BaseFilter<T> inner, FilterMatcher<T> matcher) {
		super();
		this.inner = inner;
		this.matcher = matcher;
	}
	private final BaseFilter<T> inner;
	private final FilterMatcher<T> matcher;
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter#acceptVisitor(uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor)
	 */
	@Override
	public <X> X acceptVisitor(FilterVisitor<X,T> vis) throws Exception {
		return vis.visitAcceptFilter(this);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Targetted#getTarget()
	 */
	@Override
	public Class<T> getTarget() {
		return inner.getTarget();
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.AcceptFilter#accept(java.lang.Object)
	 */
	@Override
	public boolean accept(T o) {
		return matcher.matches(inner, o);
	}
	
}
