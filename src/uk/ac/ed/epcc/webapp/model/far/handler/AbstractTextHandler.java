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
package uk.ac.ed.epcc.webapp.model.far.handler;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Text;

import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.model.far.response.ResponseDataManager;
import uk.ac.ed.epcc.webapp.model.far.response.StringDataManager;

/** Abstract superclass for {@link Text} inputs that don't need configuration.
 * @author spb
 *
 */
public abstract class AbstractTextHandler implements QuestionFormHandler<String> {

	/**
	 * 
	 */
	public AbstractTextHandler() {
		super();
	}

	@Override
	public Class<? super String> getTarget() {
		return String.class;
	}

	@Override
	public void buildConfigForm(Form f) {
		
	}

	@Override
	public Class<? extends ResponseDataManager> getDataClass() {
		return StringDataManager.class;
	}

	@Override
	public boolean hasConfig() {
		return false;
	}

}