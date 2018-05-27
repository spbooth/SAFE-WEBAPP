//| Copyright - The University of Edinburgh 2016                            |
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
package uk.ac.ed.epcc.webapp.forms.html;

import java.util.Collection;
import java.util.Map;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.action.FormAction;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory;

/** A {@link FormAction} that provides custom handling of a form that fails to validate.
 * 
 * This behaviour only applies in the web-context where form submission and validation are seperate.
 * @author spb
 *
 */
public abstract class ErrorProcessingFormAction<X,K> extends FormAction{
/** Process a form that failed to validate.
 * 
 * The default re-display of the form can be triggered by returning a {@link ErrorFormResult}.
 * 
 * @param conn {@link AppContext}
 * @param f {@link Form}
 * @param target 
 * @param missing  required fields with no value
 * @param errors map of field names to validation errors.
 * @return {@link FormResult}
 */
	public FormResult processError(AppContext conn,Form f, TransitionFactory<K,X> provider,X target,K key,Collection<String> missing, Map<String,String> errors){
		return new ErrorFormResult<X, K>(provider, target, key, errors, missing);
	}
}
