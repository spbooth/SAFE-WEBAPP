//| Copyright - The University of Edinburgh 2020                            |
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

import java.util.Iterator;

/** An {@link ItemInput} that suggests possible items.
 * 
 * The input may also allow additional items beyond the suggested set but the suggestions should
 * not include invalid items.
 * @author Stephen Booth
 *
 */
public interface SuggestedItemInput<V, T> extends ItemInput<V, T> {
	/**
	 * get a list of domain objects that are being selected
	 * 
	 * @return Iterator
	 */
	public abstract Iterator<T> getItems();

	/** get the number of suggested items.
	 * null selections don't count.
	 * 
	 * @return int
	 */
	public int getCount();
	
	
	
}
