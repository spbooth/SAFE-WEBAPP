//| Copyright - The University of Edinburgh 2017                            |
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

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.AppUserFactory;
import uk.ac.ed.epcc.webapp.session.AppUserNameFinder;
import uk.ac.ed.epcc.webapp.session.Hash;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** An implementation of the IdP end of the remote authentication protocol used by {@link RemoteAuthServlet}.
 * 
 * This is if we wish to implement this protocol between two safe versions.
 * 
 * Parameters are:
 * <ul>
 * <li> <b>remote_auth_servlet.url</b> url of RemoteAuthServlet</li>
 * <li> <b>remote_auth_servlet.realm</b> realm to generate name from.</li>
 * <li> <b>remote_auth_servlet.secret</b> Secret to use in auth</li>
 * <li> <b>remote_auth_servlet.hash</b> Hash algorithm to use</li>
 * 
 * </ul>
 * 
 * 
 * 
 * @author spb
 *
 */
public class BounceAuthServlet extends SessionServlet {

	/**
	 * 
	 */
	public BounceAuthServlet() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.servlet.SessionServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, uk.ac.ed.epcc.webapp.AppContext, uk.ac.ed.epcc.webapp.session.SessionService)
	 */
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res, AppContext conn, SessionService person)
			throws Exception {
		ServletService serv = conn.getService(ServletService.class);
		Map<String,Object> params = serv.getParams();
		String token = (String) params.get("token");
		if( token == null || token.isEmpty()){
			message(conn, req, res, "invalid_argument");
			return;
		}
		String remote_url = conn.getInitParameter("remote_auth_servlet.url");
		
		if( remote_url == null ){
			getLogger(conn).error("No remote url");
			return;
		}
		String realm = conn.getExpandedProperty("remote_auth_servlet.realm");
		AppUserFactory fac = conn.getService(SessionService.class).getLoginFactory();
		AppUserNameFinder<AppUser, ?> realmFinder = fac.getRealmFinder(realm);
		if( realm == null || realmFinder == null ){
			getLogger(conn).error("No realm");
			return;
		}
		
		String name = realmFinder.getCanonicalName(person.getCurrentPerson());
		if( name == null ){
			message(conn, req, res, "invalid_argument");
			return;
		}
		String auth_secret = conn.getInitParameter("remote_auth_servlet.secret","");
		Hash h = conn.getEnumParameter(Hash.class, "remote_auth_servlet.hash", Hash.SHA512);
		String check_token = h.getHash(name+token+auth_secret);
		String return_url = remote_url+"?check_token="+check_token+"&auth_name="+name;
		// Don't encode the URL as it is a known cross-site redirect
		res.sendRedirect(return_url);
		
	}

}
