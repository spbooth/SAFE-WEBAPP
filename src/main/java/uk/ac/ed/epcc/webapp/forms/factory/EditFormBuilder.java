//| Copyright - The University of Edinburgh 2015                            |
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
package uk.ac.ed.epcc.webapp.forms.factory;

import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** interface for classes that build edit/update forms.
 * @author spb
 *
 * @param <T>
 */

public interface EditFormBuilder<T> extends FormFactory {

	/**
	 * Build a form for updating an object including the action buttons.
	 
	 * @param f
	 *            Form to build
	 * @param dat
	 *            Object we are editing.
	 *            
	 * @param operator
	 *             person editing the form
	 * @throws Exception
	 */
	public abstract void buildUpdateForm( Form f, T dat,SessionService<?> operator)
			throws Exception;

}