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

import java.util.HashMap;
import java.util.Map;

import uk.ac.ed.epcc.webapp.CurrentTimeService;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.SerializableFormResult;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.servlet.RequiredPageServlet;
import uk.ac.ed.epcc.webapp.servlet.session.ServletSessionService;
import uk.ac.ed.epcc.webapp.session.AbstractSessionService;
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
	public static final String AUTH_USES_2FA_ATTR="TwoFactorUsed";
   /**
	 * @param sess
	 */
	public TwoFactorHandler(SessionService<A> sess) {
		super();
		this.sess = sess;
	}

    private final SessionService<A> sess;
   
    /** would a call to {@link #doLogin(AppUser, String,SerializableFormResult)}
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
    
    public boolean enabled(A user) {
    	AppUserFactory<A> person_fac = sess.getLoginFactory();
    	for( TwoFactorComposite<A> comp : person_fac.getComposites(TwoFactorComposite.class)) {
			if( comp.enabled(user)) {
				return true;
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
    private void completeAuth(A user) {
    	AppUserFactory<A> person_fac = sess.getLoginFactory();
    	boolean is_super=false;
		if( sess instanceof ServletSessionService) {
			is_super = ((ServletSessionService<A>)sess).isSU();
		}
		if( ! is_super) {
			for( TwoFactorComposite<A> comp : person_fac.getComposites(TwoFactorComposite.class)) {
				comp.completeAuth(user);
			}
		}

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
					Map logattr = new HashMap();
					// Need two factor
					sess.setAttribute(AUTH_USER_ATTR, user.getID());
					logattr.put(AbstractSessionService.person_tag,user.getID());
					logattr.put("user", user.getIdentifier());
					sess.setAttribute(AUTH_TYPE_ATTR, type);
					logattr.put(AbstractSessionService.auth_type_tag,type);
					if( next_page != null ) {
						sess.setAttribute(AUTH_RESULT_ATTR, next_page);
					}else {
						sess.removeAttribute(AUTH_RESULT_ATTR);
					}
					// session is not populated so we need to set the context manually
					securityEvent("SucessfulAuthentication",logattr);
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
		securityEvent("SucessfulAuthentication");
		
		// consider if we need to go to the required page servlet as well
		next_page = RequiredPageServlet.getNext(sess, next_page);
		return next_page;
	
	
	}

    /** Log the user in asserting that two-factor login has already taken place
     * 
     * @param user
     * @param type
     * @param next_page
     * @return
     */
    public FormResult assertTwoFactorLogin(A user,String type,SerializableFormResult next_page) {
		 
  	
  		if( ! sess.haveCurrentUser()) {
  			sess.setCurrentPerson(user);
  			CurrentTimeService time = sess.getContext().getService(CurrentTimeService.class);
  			if( time != null) {
  				sess.setAuthenticationTime(time.getCurrentTime());
  			}
  			sess.setAuthenticationType(type);
  			recordTwoFactor();
  		}
  		securityEvent("SucessfulAuthentication - asserted");
  		next_page = RequiredPageServlet.getNext(sess, next_page);
  		return next_page;
  	
  	
  	}

    /** record 2 factor login in the session
     * 
     */
	public void recordTwoFactor() {
		sess.setAttribute(AUTH_USES_2FA_ATTR, Boolean.TRUE);
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
						sess.setAuthenticationType(type);
					}
					recordTwoFactor();
					completeAuth(user);
					securityEvent("Sucessful2FA");
				}else {
					A user = sess.getLoginFactory().find(requested_id);
					Map attr = new HashMap();
					attr.put("user",user.getIdentifier());
					securityEvent("Failed2FA", attr);
				}
				SerializableFormResult result = (SerializableFormResult) sess.getAttribute(AUTH_RESULT_ATTR);
				// consider if we need to go to the required page servlet as well
				result = RequiredPageServlet.getNext(sess, result);
				return result;
			}
		}finally {
			sess.removeAttribute(AUTH_RESULT_ATTR);
			sess.removeAttribute(AUTH_USER_ATTR);
			sess.removeAttribute(AUTH_TYPE_ATTR);
		}
		return null;
	}

	public void securityEvent(String event) {
		sess.getContext().getService(LoggerService.class).securityEvent(event,sess);
	}
	public void securityEvent(String event,Map attr) {
		sess.getContext().getService(LoggerService.class).securityEvent(event,sess,attr);
	}
	/**
	 * @return
	 */
	public Logger getLogger() {
		return sess.getContext().getService(LoggerService.class).getLogger(getClass());
	}
	/** Query the session to see if TwoFactor authentication was used on login.
	 * 
	 * @param sess
	 * @return
	 */
	public static boolean usedTwoFactor(SessionService<?> sess) {
		if( sess == null) {
			return false;
		}
		Boolean b = (Boolean) sess.getAttribute(AUTH_USES_2FA_ATTR);
		if( b != null && b.booleanValue()) {
			return true;
		}
		return false;
	}
}
