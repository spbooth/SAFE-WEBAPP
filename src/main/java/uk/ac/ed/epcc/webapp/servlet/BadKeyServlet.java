//| Copyright - The University of Edinburgh 2020                            |
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

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.ssh.BadKeyFactory;

/** Servlet to generate the fingerprints of ssh keys forbidden by this server
 * 
 * It is unauthenticated as only hashes are published
 * @author Stephen Booth
 *
 */
@WebServlet(name="BadKeyServlet", urlPatterns = "/ForbiddenKeyHashes")
public class BadKeyServlet extends WebappServlet {

	public static final Feature PUBLISH_BAD_KEYS = new Feature("ssl.publish_bad_keys",false,"Public bad keys publicly");
	/**
	 * 
	 */
	public BadKeyServlet() {
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.servlet.WebappServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, uk.ac.ed.epcc.webapp.AppContext)
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res, AppContext conn)
			throws ServletException, IOException {
		if( ! PUBLISH_BAD_KEYS.isEnabled(conn)) {
			res.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
			return;
		}
		BadKeyFactory fac = new BadKeyFactory(conn);
		res.setContentType("text/plain");
		PrintWriter w = res.getWriter();
		try {
			for(BadKeyFactory.BadKey key : fac.all()) {
				w.println(key.getFingerprint());
			}
		} catch (DataFault e) {
			getLogger(conn).error("Error generating badkey fingerprints", e);
		}
		
		w.close();

	}

}
