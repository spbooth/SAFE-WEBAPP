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

// A text input with auto-complete suggestions
package uk.ac.ed.epcc.webapp.forms.inputs;

import java.util.Set;

/** A {@link TextInput} with an auto-complete list.
 * 
 * This may also be displayed as a text-box plus pull-down. It is an {@link ItemInput}
 * to allow the suggestions to show expanded selection text but it it possible to set String values that
 * don't correspond to an Item.
 * @author James
 *
 */
public abstract class AutocompleteTextInput<T> extends TextInput implements ItemInput<String,T>, AutoComplete<T,String> {
	public AutocompleteTextInput(boolean allow_null) {
		super(allow_null);
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.AutoComplete#getSuggestions()
	 */
	// subclasses should override to return a list of possible completions
	@Override
	abstract public Set<T> getSuggestions();
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.AutoComplete#getValue(T)
	 */
	@Override
	public abstract String getValue(T item);

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.AutoComplete#getSuggestionText(T)
	 */
	@Override
	public String getSuggestionText(T item){
		return getValue(item);
	}

	

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.ItemInput#setItem(java.lang.Object)
	 */
	@Override
	public void setItem(T item) {
		setValue(getValue(item));
		
	}
}
