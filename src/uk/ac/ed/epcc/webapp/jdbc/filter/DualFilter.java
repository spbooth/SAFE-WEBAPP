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

/** Interface that marks a Filter that can perform the same selection
 * using either a {@link PatternFilter} or an {@link AcceptFilter}.
 * 
 * In most cases this will default to behaving as a {@link PatternFilter} 
 * but a {@link FilterVisitor} can select the {@link AcceptFilter} behaviour
 * in cases where that would allow a database operation to be skipped entirely.
 * @author spb
 *
 */
public interface DualFilter<T> extends PatternFilter<T>, AcceptFilter<T>{

}
