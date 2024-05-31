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
package uk.ac.ed.epcc.webapp.forms.inputs;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.model.data.forms.Selector;

/** Input for a list of tags that must 
 * resolve to objects assignable to a particular type.
 * 
 * @author spb
 *
 */
public class ObjectListInput extends TextInput {
	public ObjectListInput(AppContext conn,Class<?> target) {
		super();
		setSingle(true);
		addValidator(new ObjectListValidator(conn, target));
	}


	public static Selector<ObjectListInput> getSelector(AppContext conn,Class<?> target){
		return new Selector<ObjectListInput>() {

			@Override
			public ObjectListInput getInput() {
				return new ObjectListInput(conn, target);
			}
			
		};
	}
}