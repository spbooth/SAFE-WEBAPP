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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.LinkedList;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;

import uk.ac.ed.epcc.webapp.AppContextService;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.stream.ByteArrayMimeStreamData;
import uk.ac.ed.epcc.webapp.model.data.stream.MimeStreamData;
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
	
	/** form parameter for the default payload.
	 * unencoded PUT data is mapped to this param
	 * 
	 */
	String DEFAULT_PAYLOAD_PARAM = "update";

	/** request attribute for a custom message to add
	 * to errorpage content
	 * 
	 */
	String ERROR_MSG_ATTR = "uk.ac.ed.epcc.webapp.error.message";

	String ARG_TERRMINATOR = "-";
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
   
    /** Redirect to an external URI
     * 
     * This is intended for external URLs so session is never encoded
     * 
     * @param uri
     */
    public void redirect(URI uri) throws IOException;
	
	/**
	 * Extract a Map of the request parameters from the request. This is made a
	 * method on the ServletAppContext to allow access to logging/configuration
	 * and to allow the application specific customisation.
	 * 
	 * @return Map of parameters
	 * @throws DataFault
	 */
	
	public Map<String,Object> getParams() ;
	
	/** get a named paramter as a string
	 * 
	 * This should include conversion f uplaoded files etc.
	 * 
	 * @param name
	 * @return
	 */
	default public String getTextParameter(String name) {
		Object o = getParams().get(name);
		if( o == null ) {
			return null;
		}
		if( o instanceof String) {
			return (String) o;
		}
		if( o instanceof MimeStreamData) {
			MimeStreamData msd= (MimeStreamData) o;
			if( msd.getContentType().contains("text")) {
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				try {
					msd.write(stream);
					return stream.toString("UTF-8");
				} catch (Exception e) {
					getContext().getService(LoggerService.class).getLogger(getClass()).error("error converting to string",e);
					return null;
				}
			}
		}
		return o.toString();
	}
	default public MimeStreamData getStreamParam(String name) throws DataFault{
		Object o = getParams().get(name);
		if( o == null ) {
			return null;
		}
		if( o instanceof MimeStreamData) {
			return (MimeStreamData) o;
		}
		if( o instanceof String) {
			ByteArrayMimeStreamData result = new ByteArrayMimeStreamData(((String)o).getBytes());
			result.setMimeType("text/plain");
			return result;
		}
		throw new DataFault("Unsupported object "+o.getClass().getCanonicalName());
	}
	
	/** Get the ServletPath as a list of strings
	 * A path element of "-" terminates the list. 
	 * This is to allow parameters to be passed on the 
	 * path at the same time as an arguemnt path.
	 * @return LinkedList<String> arguments in order.
	 */
    public LinkedList<String> getArgs();
    
    /** Get the remaining path after arguments have been extracted
     * ie the ServletPath after the first "-" element.
     * 
     * @return
     */
    public String getFilePath();
	/**
	 * get the authenticated name for the current user as provided by the
	 * web-server/container authorisation layer. This will be null unless the
	 * container/web-server has authorisation turned on for this URL.
	 * 
	 * @return String webname
	 */
	public String getWebName() ;
	/**
	 * get the authenticated name for the current user as provided by the
	 * web-server/container authorisation layer. This will be null unless the
	 * container/web-server has authorisation turned on for this URL.
	 * 
	 * This method takes an explicit {@link ServletRequest} object rather than
	 * the one cached in the service itself. It can therefore be used when a
	 * filter may be in place.
	 * 
	 * 
	 * @param ServletRequest
	 * @return String webname
	 */
	public String getWebName(ServletRequest req);
	/** Populate a session automatically using information from the request.
	 * This is invoked by the {@link ServletSessionService} if a person is requested and
	 * the current person is not stored in the session. It handles authentication mechanisms that don't
	 * use a specific login url.
	 * 
	 * If the session is not populated here it may trigger a call to {@link #requestAuthentication(SessionService)} later.
	 * Any authentication errors could be cached in the request and handled there.
	 * 
	 * @param sess {@link SessionService}
	 */
	public <A extends AppUser> void populateSession(SessionService<A> sess);

	/** Authentication is required for this request but credentials are not available.
	 * If possible this should request the client to re-send the request with correct
	 * authorisation. e.g. by redirecting to a login page or requesting basic authentication.
	 * 
	 * The request could take account of authentication errors from an earlier call to {@link #populateSession(SessionService)}. These can be cahce din the request.
	 * 
	 * The {@link SessionService} is provided as a parameter to be queried for its capabilities.
	 * 
	 * @param sess {@link SessionService}
	 * @throws IOException 
	 * @throws ServletException 
	 * 
	 */
	public <A extends AppUser> void requestAuthentication(SessionService<A> sess) throws IOException, ServletException;
	/** Go to the login page to request a login.
	 * 
	 * @param <A>
	 * @param sess {@link SessionService}
	 * @param page  page to return to
	 * @throws IOException
	 * @throws ServletException
	 */
	public <A extends AppUser> void requestLogin(SessionService<A> sess, String page)
			throws IOException, ServletException;
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
	
	public Iterable<String> getAttributeNames();
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
	
	/** Set an inactivity timeout if supported
	 * 
	 * @param seconds
	 */
	public void setTimeout(int seconds);
	
	/** Send an error reponse.
	 * The message is included in the response header but
	 * also included in the html error page
	 * 
	 * @param code  Http error code
	 * @param message custom message
	 * @throws IOException 
	 */
	public void sendError(int code, String message) throws IOException;
}