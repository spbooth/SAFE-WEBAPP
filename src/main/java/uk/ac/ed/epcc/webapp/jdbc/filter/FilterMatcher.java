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

/** An interface for an object that can test if a filter matches a specific object.
 *
 * For example a factory object can combine the filter with another that only selects the
 * candidate and see if that returns a result.
 * @author spb
 * @param <T> type of filter/target
 *
 */
public interface FilterMatcher<T> {
	public boolean matches(BaseFilter<? super T> fil, T o);
}
