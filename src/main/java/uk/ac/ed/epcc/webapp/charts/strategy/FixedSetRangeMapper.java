//| Copyright - The University of Edinburgh 2019                            |
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
package uk.ac.ed.epcc.webapp.charts.strategy;

/** a {@link SetRangeMapper} that only generates a single set.
 * 
 * 
 * This is used to add the required set even when no data is added for consistency with {@link QueryMapper}
 * 
 * @author Stephen Booth
 *
 */
public interface FixedSetRangeMapper<T> extends SetRangeMapper<T> {

	/** get the set generated
	 * 
	 * @return
	 */
	public int getFixedSet();
}
