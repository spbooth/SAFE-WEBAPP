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
package uk.ac.ed.epcc.webapp.servlet.navigation;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.servlet.session.ServletSessionService;
import uk.ac.ed.epcc.webapp.session.SessionService;

/**
 * @author spb
 *
 */
public class SUNode extends ExactNode {

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.servlet.navigation.Node#getMenuText(uk.ac.ed.epcc.webapp.AppContext)
	 */
	@Override
	public String getMenuText(AppContext conn) {
		SessionService sess = conn.getService(SessionService.class);
		if( sess != null && sess instanceof ServletSessionService && ((ServletSessionService)sess).isSU()){
			return ((ServletSessionService)sess).getCurrentPerson().getIdentifier();
		}
		return super.getMenuText(conn);
	}

	public SUNode() {
		super();
	}

}
