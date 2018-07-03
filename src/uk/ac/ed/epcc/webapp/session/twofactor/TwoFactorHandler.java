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

import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.SerializableFormResult;
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
 * enabled on any location we need to implement via a required-page instead
 * @author Stephen Booth
 *
 */
public class TwoFactorHandler<A extends AppUser> {
	public static final String AUTH_USER_ATTR="TwoFactorCandidate";
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

    public FormResult doLogin(A user,SerializableFormResult next_page) {
		 
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
		}
	
		return next_page;
	
	
	}


	public FormResult completeTwoFactor(boolean success) {
		try {
			// We could loop over the composites again
			// to support more than 2 factors.  Keep thing simple for now
			if( success) {
				if(  ! sess.haveCurrentUser()) {
					sess.setCurrentPerson((Integer)sess.getAttribute(AUTH_USER_ATTR));
				}
				FormResult result = (FormResult) sess.getAttribute(AUTH_RESULT_ATTR);
				return result;
			}
		}finally {
			sess.removeAttribute(AUTH_RESULT_ATTR);
			sess.removeAttribute(AUTH_USER_ATTR);
		}
		return null;
	}
}
