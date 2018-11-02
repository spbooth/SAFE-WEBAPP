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
package uk.ac.ed.epcc.webapp.forms.transition;

import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;

/** A simple abstract sub-class for {@link DirectTargetlessTransition}s
 * 
 * This saves a small amount of boiler plate when no other superclass is required.
 * @author spb
 * @param <X> 
 *
 */

public abstract class AbstractDirectTargetlessTransition<X> implements
		DirectTargetlessTransition<X> {

	@Override
	public final FormResult getResult(TransitionVisitor<X> vis)
			throws TransitionException {
		return vis.doDirectTargetlessTransition(this);
	}


}