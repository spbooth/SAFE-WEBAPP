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
package uk.ac.ed.epcc.webapp.servlet.session.token;

import uk.ac.ed.epcc.webapp.AppContextService;
import uk.ac.ed.epcc.webapp.PreRequisiteService;
import uk.ac.ed.epcc.webapp.servlet.ServletService;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** A {@link AppContextService} that encapsulates the policy for bearer token authentication. 
 * 
 * The model here is that bearer tokens can modify the session. Typically setting the current user.
 * The role session roles may also be modified to fine tune permissions for example having a token that removes
 * some of the users roles. Alternatively a user could not be set, with just session roles or explicit decoding of the
 * access token used for access control.
 * 
 * Normally bearer tokens will not establish a session but each request will authenticate. Different operations can then
 * be restricted to different scopes (with a full impersonation scope being the default requirement). Normal user permissions
 * will still apply but the token will only authenticate if it has at least one of the desired scopes.
 * 
 * Different models could be mixed in the same application provided that the tokens always inhabit different realms.
 * 
 * 
 * @author Stephen Booth
 *
 */
@PreRequisiteService(ServletService.class)
public interface BearerTokenService extends AppContextService<BearerTokenService> {

	/** Enable/Disable bearer token authentication for this request.
	 * the service will probably have a configurable default but this allows bearer tokens to be enabled/disabled
	 * for specific operations explicitly.
	 * 
	 * @param active
	 */
	public void setActive(boolean active);
	
	
	
	/** get the realm to request for bearer token authentication.
	 * 
	 * Realms are distinct token spaces. They may be set per url so only a sub-set of
	 * urls support bearer authentication. Not clear if the realm part of the standard is use much
	 * 
	 * @return realm name or null
	 */
	public String getRealm();
	
	/** Return the scopes requested during authentication.
	 * As these are optional in the request this may return null or an empty array.
	 * If specified the token should only authenticate if it has one of the specified scopes.
	 * 
	 * @return String array of scopes
	 */
	public String[] requestedScopes();
	
	/** restrict authentication to tokens that have one of the specified scopes.
	 * 
	 * @param scopes
	 */
	public void setRequiredScopes(String scopes[]);
	/** get an error  to return with authentication request. This should only return a non-null value
	 * after {@link #processToken(SessionService, String)} has failed to validate a token.
	 * 
	 * @return
	 */
	public ErrorCodes getError();
	
	/** Attempt to login with the specified token.
	 * 
	 * If this succeeds then the session should be modified appropriately (in principle we could support setting roles for anonymous users)
	 * {@link #request()} should return false after this.
	 * 
	 * If it fails then {@link #request()} should return true and {@link #getError()} may return a non-null value. 
	 * 
	 * This method <b>MUST</b> verify the permitted scopes of the token.
	 * 
	 * @param SessionService to modify
	 * @param token
	 */
	public void processToken(SessionService sess, String token);
	
	/** Should authentication be requested or errors reported.
	 * If this returns false then no authentication should be requested. 
	 * This should return false after {@link #processToken(SessionService, String)}
	 * has successfully processed a token.
	 * This should always return true if there is an error to report.
	 * 
	 * If this returns false then bearer authentication will not be explicitly requested but a client can still provide a
	 * token spontaneously provided the realm is non null.
	 * 
	 * @return boolean
	 */
	public boolean request();
	
	/** get the successfully processed token.
	 * 
	 * This is to support cases where the token needs to be decoded to obtain additional permission information.
	 * 
	 * @return token
	 */
	public String getToken();
	
	/** Does the authenticated token have the specified scope.
	 * 
	 * This is for fine tuing access control where the url permits multiple scopes.
	 * 
	 * @param scope
	 * @return
	 */
	public boolean hasScope(String scope);
}
