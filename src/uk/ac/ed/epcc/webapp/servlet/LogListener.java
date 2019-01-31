//| Copyright - The University of Edinburgh 2019                            |
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

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import uk.ac.ed.epcc.webapp.AbstractContexed;
import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.session.AppUser;

/**
 * @author Stephen Booth
 *
 */
public class LogListener extends AbstractContexed implements RemoteAuthListener {

	private final String tag;
	/**
	 * @param conn
	 */
	public LogListener(AppContext conn,String tag) {
		super(conn);
		// TODO Auto-generated constructor stub
		this.tag=tag;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.servlet.RemoteAuthListener#authenticated(uk.ac.ed.epcc.webapp.session.AppUser)
	 */
	@Override
	public void authenticated(AppUser user) {
		Logger log = getLogger();
		ServletService serv = getContext().getService(ServletService.class);
		log.debug("remote authentication to "+tag+" "+user.getIdentifier());
		for(String name : serv.getAttributeNames()) {
			log.debug(name+":="+serv.getRequestAttribute(name));
		}
		Map props = new HashMap();
		serv.addErrorProps(props);
		for(Object name : props.keySet()) {
			log.debug(name.toString()+":="+props.get(name));
		}
	}

}
