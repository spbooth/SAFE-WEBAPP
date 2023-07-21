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
package uk.ac.ed.epcc.webapp.forms.inputs;

import java.util.Set;

import uk.ac.ed.epcc.webapp.forms.TestDataProvider;

/**
 * @author Stephen Booth
 *
 */
public interface BoundedInputDataProvider<T extends Comparable<T>, I extends BoundedInput<T>> extends TestDataProvider<T, I> {

	/** get a lower bound
	 * 
	 * @return
	 */
	public T getLowBound();
	/** get an upper bound value
	 * 
	 * @return
	 */
	public T getHighBound();
	/** get good data above the higher bound
	 * 
	 * @return
	 */
	public Set<T> getHighData();
	/** get good data below the lower bound
	 * 
	 * @return
	 */
	public Set<T> getLowData();
}
