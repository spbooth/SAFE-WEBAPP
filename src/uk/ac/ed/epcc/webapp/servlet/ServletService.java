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
	 *            String page to forward to
	
	 * @throws ServletException
	 * @throws IOException
	 */
	public void forward(String url) throws ServletException, IOException ;
	
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
	 * 
	 */
	public <A extends AppUser> void requestAuthentication(SessionService<A> sess) throws IOException;
	
	/** Return the default charset we want to use.
	 * 
	 * @return
	 */
	public String defaultCharset();
}
