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
	 * @see uk.ac.ed.epcc.webapp.Targetted#getTarget()
	 */
	@Override
	public String getTag() {
		return inner.getTag();
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.filter.AcceptFilter#accept(java.lang.Object)
	 */
	@Override
	public boolean test(T o) {
		return matcher.matches(inner, o);
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((inner == null) ? 0 : inner.hashCode());
		result = prime * result + ((matcher == null) ? 0 : matcher.hashCode());
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
		ConvertToAcceptFilter other = (ConvertToAcceptFilter) obj;
		if (inner == null) {
			if (other.inner != null)
				return false;
		} else if (!inner.equals(other.inner))
			return false;
		if (matcher == null) {
			if (other.matcher != null)
				return false;
		} else if (!matcher.equals(other.matcher))
			return false;
		return true;
	}
	
}
