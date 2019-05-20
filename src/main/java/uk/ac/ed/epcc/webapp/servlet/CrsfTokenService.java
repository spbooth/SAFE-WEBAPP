//| Copyright - The University of Edinburgh 2018                            |
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
package uk.ac.ed.epcc.webapp.servlet;

import uk.ac.ed.epcc.webapp.AppContextService;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory;

/** An {@link AppContextService} to generate CRSF tokens
 * @author Stephen Booth
 *
 */
public interface CrsfTokenService extends AppContextService<CrsfTokenService> {

	/** Get a crsf token for the operation.
	 * 
	 * If this method returns null the check is disabled.
	 * 
	 * This may just remove a unique token for the session or it might return a different
	 * token for each provider/target. We don't mutate by transition as the view_target
	 * page uses a single form for multiple transitions selected by submit input. If these are direct
	 * transitions then they would need to validate the token.
	 * 
	 * @param provider
	 * @param target
	 * @return token or null
	 */
	public <K,T,P extends TransitionFactory<K, T>> String getCrsfToken(P provider, T target);
}
