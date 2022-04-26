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
package uk.ac.ed.epcc.webapp.servlet.navigation;

import uk.ac.ed.epcc.webapp.servlet.ServletService;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** An {@link ExactNode} that records the calling page.
 * @author spb
 *
 */

public class PageNode extends ExactNode {

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.servlet.navigation.Node#getTargetURL(uk.ac.ed.epcc.webapp.servlet.ServletService)
	 */
	@Override
	public String getTargetURL(ServletService servlet_service) {
		String page = servlet_service.encodePage();
		if( ! page.startsWith("/")) {
			page = "/"+page;
		}
		return super.getTargetURL(servlet_service)+"/"+ServletService.ARG_TERRMINATOR+page;
	}

}