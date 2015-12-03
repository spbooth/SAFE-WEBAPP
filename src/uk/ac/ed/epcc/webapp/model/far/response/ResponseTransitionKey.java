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
package uk.ac.ed.epcc.webapp.model.far.response;

import uk.ac.ed.epcc.webapp.model.data.transition.TransitionKey;
import uk.ac.ed.epcc.webapp.model.far.DynamicFormManager.DynamicForm;
import uk.ac.ed.epcc.webapp.model.far.response.ResponseManager.Response;
import uk.ac.ed.epcc.webapp.session.SessionService;

/**
 * @author spb
 *
 */

public class ResponseTransitionKey<D extends DynamicForm, R extends Response<D>> extends TransitionKey<ResponseTarget<D,R>> {

	/**
	 * @param t
	 * @param name
	 * @param help
	 */
	public ResponseTransitionKey(String name, String help) {
		super(ResponseTarget.class, name, help);
	}

	/**
	 * @param t
	 * @param name
	 */
	public ResponseTransitionKey(String name) {
		super(ResponseTarget.class, name);
	}
	
	public boolean allow(ResponseTarget<D, R> target, SessionService<?> sess){
		return target.getResponse().canEdit(sess);
	}

}