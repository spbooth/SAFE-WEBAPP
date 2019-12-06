//| Copyright - The University of Edinburgh 2013                            |
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

/** Input for phone numbers
 * @author spb
 *
 */

public class PhoneInput extends PatternTextInput implements HTML5Input {

	/**
	 * @param pattern
	 */
	public PhoneInput() {
		super("^\\+[\\d ]+$");
		setTag("+ followed by numbers and spaces");
		setBoxWidth(15);
		setSingle(true);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.HTML5Input#getType()
	 */
	@Override
	public String getType() {
		return "tel";
	}

	

}