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
package uk.ac.ed.epcc.webapp.forms.action;

import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.InvalidInputResult;
import uk.ac.ed.epcc.webapp.forms.result.MessageResult;

/** A {@link FormAction} used to denote a disabled (but present) action.
 * @author spb
 *
 */

public final class DisabledAction extends FormAction {

	private String help;
	/**
	 * 
	 */
	public DisabledAction() {
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.action.FormAction#action(uk.ac.ed.epcc.webapp.forms.Form)
	 */
	@Override
	public FormResult action(Form f) throws ActionException {
		return new InvalidInputResult();
	}

	public void setHelp(String help){
		this.help=help;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.action.FormAction#getHelp()
	 */
	@Override
	public String getHelp() {
		return help;
	}

}