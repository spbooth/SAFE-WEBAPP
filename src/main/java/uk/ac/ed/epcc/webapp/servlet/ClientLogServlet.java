package uk.ac.ed.epcc.webapp.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Feature;
/** A servlet that accepts logging messages (in JSON) 
 * This is intended for logging from browsers (e.g. Content Security Policy reporting)
 * or client javascript
 * 
 * @author Stephen Booth
 *
 */
@WebServlet(displayName =  "ClientLogServlet", urlPatterns =  "/ClientLogs")
public class ClientLogServlet extends WebappServlet {

	public static final Feature CLIENT_LOG = new Feature("client_log.servlet",true,"Allow remote client logging");
	public ClientLogServlet() {
	}

	

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res, AppContext conn)
			throws ServletException, IOException {
		
		
		ServletService ss = conn.getService(ServletService.class);
		if( ss != null) {
			if( ! CLIENT_LOG.isEnabled(conn)) {
				ss.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, "");
				return;
			}
			String input = ss.getTextParameter(ServletService.DEFAULT_PAYLOAD_PARAM,true);
			if( input.length() > conn.getIntegerParameter("client_log.max_message", 4096)) {
				ss.sendError(HttpServletResponse.SC_BAD_REQUEST, "Message too long");
				return;
			}
			if( input != null ) {
				getLogger(conn).warn(input);
			}
		}
		ss.sendError(HttpServletResponse.SC_OK, "Message received");
		
	}


}
