//| Copyright - The University of Edinburgh 2011                            |
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
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import uk.ac.ed.epcc.webapp.AppContext;

/** Servlet that expects to be authenticated at the container level
 * 
 * @author spb
 *
 */
public abstract class ContainerAuthServlet extends WebappServlet {

	@Override
	protected final void doPost(HttpServletRequest req, HttpServletResponse res, AppContext conn) throws ServletException, IOException {
		String user = req.getRemoteUser();
		if( verify(user )){
		   doPost(req,res,conn,user);
		   HttpSession sess = req.getSession(false);
		   return;
		}
		res.sendError(HttpServletResponse.SC_FORBIDDEN,"Not Authenticated");
		return;
	}
	/** Is this user valid, default to just checking we have a user
	 * 
	 * @param user
	 * @return boolean
	 */
    protected boolean verify(String user) {
		return user != null && user.trim().length() > 0;
	}
	protected abstract void doPost(HttpServletRequest req, HttpServletResponse res, AppContext conn,String user) throws ServletException, IOException;
}