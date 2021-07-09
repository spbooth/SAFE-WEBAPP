package uk.ac.ed.epcc.webapp.session;

import uk.ac.ed.epcc.webapp.forms.result.FormResult;

/** Interface for objects that can perform URL redirects through the login server
 * 
 * This is to avoid URL rewriting being visible in the address bar
 * that exposes the session cookie
 * Instead of forwarding directly to the target url the browser is sent to
 * the LoginServlet with parameter "authtype" set to <b><i>tag</i>:<i>id</i></b>
 * where tag is the construction tag for the implementing object and id is a
 * lookup for the target url.
 * 
 * @author Stephen Booth
 *
 */
public interface LoginRedirects {

	/** Translates an id string to a {@link FormResult}.
	 * 
	 * @param id
	 * @return
	 */
	public FormResult getRedirect(String id);
}
