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
package uk.ac.ed.epcc.webapp.forms.inputs;

import java.util.Iterator;
import java.util.Set;

/** Interface for inputs that provide auto-complete text.
 * @author spb
 * 
 * @param <V> input type
 * @param <T> item type
 * 
 */
public interface AutoComplete<V,T> extends SuggestedItemInput<V,T>, LengthInput<V> {


	/** Map an item to the corresponding value (compatible with the parse method).
	 * 
	 * @param item
	 * @return String value
	 */
	String getValue(T item);

	/** get the suggestion text. This can be an expanded form of the value
	 * 
	 * @param item
	 * @return
	 */
	String getSuggestionText(T item);

	
	/** should the auto-complete option be used?
	 * If this method returns false the input should fall back to a text
	 * input without generating the suggestion list. This is for cases where
	 * the enumerating the suggestions may be very costly.
	 * 
	 * 
	 * 
	 * @return
	 */
	default boolean useAutoComplete() {
		return true;
	}
	public default  <R> R accept(InputVisitor<R> vis) throws Exception{
		if( useAutoComplete()) {
			return vis.visitAutoCompleteInput(this);
		}else {
			return vis.visitLengthInput(this);
		}
	}
}