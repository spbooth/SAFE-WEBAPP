//| Copyright - The University of Edinburgh 2018                            |
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
package uk.ac.ed.epcc.webapp.session.twofactor;

import uk.ac.ed.epcc.webapp.CurrentTimeService;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.SerializableFormResult;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.servlet.session.ServletSessionService;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.AppUserFactory;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** Handle two-factor authorisation plugins.
 * This is only appropriate for explicit login control-flows. 
 * It is inserted at the point where the person would be set in the session.
 * If two factor is enabled the person is remembered and the two-factor verification flow invoked instead.
 * 
 * If we use external-auth
 * enabled on any location we need to invoke via a required-page instead
 * @author Stephen Booth
 *
 */
public class TwoFactorHandler<A extends AppUser> {
	public static final String AUTH_USER_ATTR="TwoFactorCandidate";
	public static final String AUTH_TYPE_ATTR="TwoFactorAuthType";
	public static final String AUTH_RESULT_ATTR="TwoFactorResult";
   /**
	 * @param sess
	 */
	public TwoFactorHandler(SessionService<A> sess) {
		super();
		this.sess = sess;
	}

    private final SessionService<A> sess;
   
    /** would a call to {@link #doLogin(AppUser, SerializableFormResult)}
     * result in an authorisation request
     * 
     * @param user
     * @return
     */
    public boolean needAuth(A user) {
    	AppUserFactory<A> person_fac = sess.getLoginFactory();
		
		boolean is_super=false;
		if( sess instanceof ServletSessionService) {
			is_super = ((ServletSessionService<A>)sess).isSU();
		}
		if( ! is_super) {
			for( TwoFactorComposite<A> comp : person_fac.getComposites(TwoFactorComposite.class)) {
				if( comp.needAuth(user)) {
					return true;
				}
			}
		}
		return false;
    }

    public boolean requireTwoFactor(A user) {
    	AppUserFactory<A> person_fac = sess.getLoginFactory();
    	boolean is_super=false;
		if( sess instanceof ServletSessionService) {
			is_super = ((ServletSessionService<A>)sess).isSU();
		}
		if( ! is_super) {
			for( TwoFactorComposite<A> comp : person_fac.getComposites(TwoFactorComposite.class)) {
				if( comp.needAuth(user)) {
					return true;
				}
			}
		}
		return false;

    }
    
    public FormResult doLogin(A user,String type,SerializableFormResult next_page) {
		 
		AppUserFactory<A> person_fac = sess.getLoginFactory();
		
		boolean is_super=false;
		if( sess instanceof ServletSessionService) {
			is_super = ((ServletSessionService<A>)sess).isSU();
		}
		if( ! is_super) {
			for( TwoFactorComposite<A> comp : person_fac.getComposites(TwoFactorComposite.class)) {
				FormResult result = comp.requireAuth(user);
				if( result != null ) {
					// Need two factor
					sess.setAttribute(AUTH_USER_ATTR, user.getID());
					sess.setAttribute(AUTH_TYPE_ATTR, type);
					if( next_page != null ) {
						sess.setAttribute(AUTH_RESULT_ATTR, next_page);
					}else {
						sess.removeAttribute(AUTH_RESULT_ATTR);
					}
					return result;
				}
			}
		}
	
		// No two factor request to process
		if( ! sess.haveCurrentUser()) {
			sess.setCurrentPerson(user);
			CurrentTimeService time = sess.getContext().getService(CurrentTimeService.class);
			if( time != null) {
				sess.setAuthenticationTime(time.getCurrentTime());
			}
			sess.setAuthenticationType(type);
		}
	
		return next_page;
	
	
	}


	public FormResult completeTwoFactor(boolean success,A expected) {
		try {
			Logger logger = getLogger();
			Integer requested_id = (Integer)sess.getAttribute(AUTH_USER_ATTR);
			String type = (String) sess.getAttribute(AUTH_TYPE_ATTR);
			if( requested_id == null || (expected != null && expected.getID() != requested_id.intValue())) {
				logger.warn("Inconsistent request and result, possible security problem "+requested_id+" "+expected.getIdentifier());
				return null;
			}
			logger.debug("success="+success);
			// We could loop over the composites again
			// to support more than 2 factors.  Keep thing simple for now
			if( success) {
				if(  ! sess.haveCurrentUser()) {
					// Use a full AppUser because there are various overrides
					// in ServletSessionServlet that break if you try to login
					// by just setting the personID
					A user = sess.getLoginFactory().find(requested_id);
					logger.debug("setting user to "+user.getIdentifier());
					sess.setCurrentPerson(user);
					CurrentTimeService time = sess.getContext().getService(CurrentTimeService.class);
					if( time != null) {
						sess.setAuthenticationTime(time.getCurrentTime());
					}
					if( type != null ) {
						sess.setAuthenticationType(AUTH_TYPE_ATTR);
					}
				}
				FormResult result = (FormResult) sess.getAttribute(AUTH_RESULT_ATTR);
				return result;
			}
		}finally {
			sess.removeAttribute(AUTH_RESULT_ATTR);
			sess.removeAttribute(AUTH_USER_ATTR);
			sess.removeAttribute(AUTH_TYPE_ATTR);
		}
		return null;
	}

	/**
	 * @return
	 */
	public Logger getLogger() {
		return sess.getContext().getService(LoggerService.class).getLogger(getClass());
	}
}
