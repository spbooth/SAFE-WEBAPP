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
package uk.ac.ed.epcc.webapp.model.far.handler;

import uk.ac.ed.epcc.webapp.Targetted;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.model.far.response.ResponseDataManager;

/**
 * @author spb
 * @param <T> type of input
 *
 */

public interface QuestionFormHandler<T> extends  Targetted<T> {
	/** build a configuration form 
	 * 
	 * @param f
	 */
	public void buildConfigForm(Form f);
	/** read the configuration from the form.
	 * the form is assumed to have validated correctly.
	 * 
	 * @param f
	 * @return true if configured ok
	 */
	public Input<T> parseConfiguration(Form f);
	
	public Class<? extends ResponseDataManager> getDataClass();
	
	/** are there any config parameters needed/valid.
	 * 
	 * @return
	 */
	public boolean hasConfig();
}