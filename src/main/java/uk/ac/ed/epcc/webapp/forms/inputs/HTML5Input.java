//| Copyright - The University of Edinburgh 2014                            |
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

import uk.ac.ed.epcc.webapp.validation.FieldValidator;

/** An Interface for {@link Input}s that can represented as html5 inputs, or
 * {@link FieldValidator}s that correspond to the validation rules of a html5 input.
 * This does not change the behaviour of the input but does assert compatibility (or not) with 
 * a corresponding html-5 input type. 
 * @author spb
 *
 */

public interface HTML5Input {
	/** get the <em>type</em> tag to emit for
	 * the corresponding html5 type. If this method returns null
	 * no type should be used. This is for the case where.
	 * a sub-class breaks compatibility and needs to suppress
	 * a type set in a superclass.
	 * 
	 * 
	 * @return name or null;
	 */
	public String getType();
}