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

/** A Node representing an external URL
 * @author spb
 *
 */

public class ExternalNode extends Node {

	/**
	 * 
	 */
	public ExternalNode() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.servlet.navigation.Node#matches(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public boolean matches(ServletService serv) {
		return false;
	}

	@Override
	public String getTargetURL(ServletService service)  {
		return getTargetPath();
	}

}