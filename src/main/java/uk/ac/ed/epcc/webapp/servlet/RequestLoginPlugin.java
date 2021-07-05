package uk.ac.ed.epcc.webapp.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.ac.ed.epcc.webapp.forms.result.FormResult;
/** Interface for plug-ins that can customise a login mechanism based
 * on information in the request.
 * 
 * Possible use cases are to use a custom login page or external auth login
 * mechanism based on a cookie or the IP address of the request.
 * 
 * @author Stephen Booth
 *
 */
public interface RequestLoginPlugin {
	/** Return a custom {@link FormResult} used to implement logins.
	 * If the pre-requisite conditions are not met then returns null
	 * and the normal login page logic is used.
	 * 
	 * @param req
	 * @param res
	 * @return {@link FormResult} or null
	 */
	public FormResult requestLogin(HttpServletRequest req, HttpServletResponse res);
}
