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

/**
 * @author James
 *
 */
public abstract class AutocompleteTextInput extends TextInput {
	public AutocompleteTextInput(boolean allow_null) {
		super(allow_null);
	}
	
	// subclasses should override to return a list of possible completions
	abstract public Set<String> getSuggestions();
}
