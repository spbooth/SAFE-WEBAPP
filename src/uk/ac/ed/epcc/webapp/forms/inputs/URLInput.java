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

import java.net.MalformedURLException;
import java.net.URL;

import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;

/** Input for URLs
 * @author spb
 *
 */

public class URLInput extends TextInput implements HTML5Input{
	
	public URLInput(){
		super();
		setSingle(true);
		setTrim(true);
	}

	@Override
	public void validate() throws FieldException {
		super.validate();
		try {
			URL url = new URL(value);
		} catch (MalformedURLException e) {
			throw new ValidateException("Bad URL", e);
		}
	}

	/**
	 * @param allow_null
	 */
	public URLInput(boolean allow_null) {
		super(allow_null);
		setSingle(true);
		setTrim(true);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.HTML5Input#getType()
	 */
	public String getType() {
		return "url";
	}

	
}