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
package uk.ac.ed.epcc.webapp.servlet;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;

import javax.servlet.ServletException;

import uk.ac.ed.epcc.webapp.AppContextService;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.servlet.session.ServletSessionService;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.SessionService;
/** An {@link AppContextService} used to access the servlet environment.
 * This is intended to abstract logic that behaves differently in HttpServlet, Web-service and
 * Portlet environments. The request/response objects are usually
 * encapsulated in this object. These should not be exposed in the external interface as
 * Portlets use different classes from servlets.
 * @author spb
 *
 */
public interface ServletService extends AppContextService<ServletService>, Contexed{
	
	/** un-encoded version of the original request page.
	 * 
	 *  * This uses a cached value because the request URL will be
	 * re-written if the request is forwarded to a different servlet/jsp.
	 * 
	 * @return String URL relative to context root.
	 * @return
	 */
	public String encodePage();
	
    
    /** Add the context path to a url path
	 * 
	 * @param url
	 * @return String
	 */
	public String encodeURL(String url);
	
	/**
	 * Forward request to a different page.
	 * 
	 * @param url
	 *            String page to forward to relative to the context path
	
	 * @throws ServletException
	 * @throws IOException
	 */
	public void forward(String url) throws ServletException, IOException ;
	/**
	 * Redirect request to a different page.
	 * 
	 * @param url
	 *            String page to forward to relative to the context path
	
	 * @throws ServletException
	 * @throws IOException
	 */
    public void redirect(String url) throws IOException ;
   
	
	/**
	 * Extract a Map of the request parameters from the request. This is made a
	 * method on the ServletAppContext to allow access to logging/configuration
	 * and to allow the application specific customisation.
	 * 
	 * @return Map of parameters
	 * @throws DataFault
	 */
	
	public Map<String,Object> getParams() ;
	
	/** Get the ServletPath as a list of strings
	 * A path element of "-" terminates the list. 
	 * This is to allow parameters to be passed on the 
	 * path at the same time as an arguemnt path.
	 * @return LinkedList<String> arguments in order.
	 */
    public LinkedList<String> getArgs();
	/**
	 * get the authenticated name for the current user as provided by the
	 * web-server/container authorisation layer. This will be null unless the
	 * container/web-server has authorisation turned on for this URL.
	 * 
	 * @return String webname
	 */
	public String getWebName() ;
	
	/** Populate a session automatically using information from the request.
	 * This is invoked by the {@link ServletSessionService} if a person is requested and
	 * the current person is not stored in the session. It handles authentication mechanisms that don't
	 * use a specific login url.
	 * 
	 * @param sess {@link SessionService}
	 */
	public <A extends AppUser> void populateSession(SessionService<A> sess);

	/** Authentication is required for this request but credentials are not available.
	 * If possible this should request the client to re-send the request with correct
	 * authorisation. e.g. by redirecting to a login page or requesting basic authentication.
	 * 
	 * The {@link SessionService} is provided as a parameter to be queried for its capabilities.
	 * 
	 * @param sess {@link SessionService}
	 * @throws IOException 
	 * @throws ServletException 
	 * 
	 */
	public <A extends AppUser> void requestAuthentication(SessionService<A> sess) throws IOException, ServletException;
	
	/** Return the default charset we want to use.
	 * 
	 * @return
	 */
	public String defaultCharset();
	
	/** Has the response been comitted.
	 * 
	 * @return
	 */
	public boolean isComitted();
	
	/** Add additional information about the request to the properties of a debugging
	 * error report email.
	 * 
	 * @param props
	 */
	public void addErrorProps(Map props);
	
	/** Identify the current request as containing sensative data that should not be cached.
	 * 
	 */
	public void noCache();
	
	/** Store an object in the current request
	 * 
	 * @param name
	 * @param value
	 */
	public void setRequestAttribute(String name, Object value);
	
	/** Retrieve an object from the current request.
	 * 
	 * @param name
	 * @return
	 */
	public Object getRequestAttribute(String name);
	
	
}