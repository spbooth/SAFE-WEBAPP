//| Copyright - The University of Edinburgh 2011                            |
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
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.servlet.session;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.CurrentTimeService;
import uk.ac.ed.epcc.webapp.PreRequisiteService;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.relationship.PersonRelationship;
import uk.ac.ed.epcc.webapp.servlet.CrossCookieComposite;
import uk.ac.ed.epcc.webapp.servlet.DefaultServletService;
import uk.ac.ed.epcc.webapp.servlet.ServletService;
import uk.ac.ed.epcc.webapp.servlet.WtmpManager;
import uk.ac.ed.epcc.webapp.servlet.WtmpManager.Wtmp;
import uk.ac.ed.epcc.webapp.servlet.navigation.NavigationMenuService;
import uk.ac.ed.epcc.webapp.session.AbstractSessionService;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.LoginObserver;
/** A SessionService for servlet context.
 * This is normally created and added to the AppContext in a Filter. 
 * 
 * Some functionality will work without a table of users configured as these can be mapped onto the underlying
 * servlet mechanisms.
 * 
 * The {@link ServletService} is given an opportunity to extract the current user from the HTTP request allowing alternate
 *  authentication mechanisms.
 * 
 * @see ServletService
 * @author spb
 *
 * @param <A> type of {@link AppUser}
 */

@PreRequisiteService(ServletService.class)
public class ServletSessionService<A extends AppUser> extends AbstractSessionService<A> {
	/**
	 * 
	 */
	public static final String WTMP_TABLE_PROP = "wtmp.table";

	/**
	 * 
	 */
	public static final String PERSON_RELATIONSHIP_TABLE = "person.relationship.table";

	public static final String BECOME_USER_ROLE = "BecomeUser";

	protected static final String SUPER_PERSON_ID_ATTR = "SuperPersonID";
	
	public static final String WTMP_ID = "SESSION_WTMP_ID";
	private static final String WTMP_EXPIRY_DATE = "SESSION_WTMP_EXPIRTY_DATE";
	private static final String NAME_ATTR="UserName";
	
	
	
	// Flag to supress re-populate if we logged out
	private boolean force_no_person=false;
	// Flag to supress read/write to http session
	// This is to force per request authentication in an api call and to prevent
	// a restricted permission api call from creating a session
	// also used to supress access to an invalidated session on logout
	private boolean use_session=true;
	private ServletService ss;
	private HttpServletRequest request;
	private String log_name = null;
  public ServletSessionService(AppContext c){
	  super(c);
	  ss = c.getService(ServletService.class);
	  if( ss instanceof DefaultServletService){
		  request = ((DefaultServletService)ss).getRequest();
	  }else{
		  request = null;
	  }
	  
  }
  
  @Override
  public String getName(){
	  if( log_name != null) {
		  return log_name;
	  }
	  String name = super.getName();
	  if( name == null ){
		  // this for when we are not using a login table.
		  return ss.getWebName();
	  }
	  return name;
  }


@Override
protected boolean shortcutTestRole(String role) {
	 if( request != null && request.isUserInRole(role)){
		  return true;
	 }
	 return false;
}

public void setAttribute(String key, Object value) {
    //need a session if we are storing anything
	HttpSession sess = null;
	if( use_session && ss instanceof DefaultServletService){
		DefaultServletService dss = (DefaultServletService)ss;
		// can't create session if comitted response
		sess = dss.getSession(! dss.isComitted());
	}
	if( value != null &&  ! (value instanceof Serializable)){
		LoggerService serv = getContext().getService(LoggerService.class);
		if( serv != null){
			serv.getLogger(getClass()).error("Non serializable object "+key+" "+value.getClass().getCanonicalName()+" added to session");
		}
	}
	if( sess != null ){
		sess.setAttribute(key, value);
	}
	
}

public void removeAttribute(String key) {
	
	HttpSession sess = null;
	if( use_session && ss instanceof DefaultServletService){
		sess = ((DefaultServletService)ss).getSession();
	}
	if( sess != null ){
		sess.removeAttribute(key);
		
	}
}

public Object getAttribute(String key) {
	
	HttpSession sess = null;
	if( use_session && ss instanceof DefaultServletService){
		sess = ((DefaultServletService)ss).getSession();
	}
	if( sess != null ){
		return sess.getAttribute(key);
	}
	return null;
}
public WtmpManager getWtmpManager() {
	AppContext c = getContext();
	String table = c.getInitParameter(WTMP_TABLE_PROP);
	if( table != null && table.length() > 0){
		return c.makeObjectWithDefault(WtmpManager.class, WtmpManager.class, table);
	}
	return null;
}

@Override
protected A lookupPerson() {
	A user = super.lookupPerson();
	if( user != null ){
		// check date to keep DB access to minimum
		Date d = (Date) getAttribute(WTMP_EXPIRY_DATE);
		if( d != null ){
			try{
				WtmpManager man = getWtmpManager();
				if( man != null ){
					CurrentTimeService time = getContext().getService(CurrentTimeService.class);
					Date now = time.getCurrentTime();
					if( d.before(now)){
						Integer id = getWtmpID();
						if( id != null ){
							Wtmp w = man.find(id);
							w.update();
						}
					}
				}
			}catch(Exception e){
				getContext().error(e, "Error updating Wtmp");
				removeAttribute(WTMP_EXPIRY_DATE);
				removeAttribute(WTMP_ID);
			}
		}

	}
	return user;
}

public Integer getWtmpID() {
	return (Integer) getAttribute(WTMP_ID);
}

@Override
public void clearCurrentPerson() {
	
	super.clearCurrentPerson();
	force_no_person=true;
	if( ! use_session) {
		return;
	}
	try{
		WtmpManager man = getWtmpManager();
		if( man != null ){
			Integer id = getWtmpID();
			if( id != null ){
				Wtmp w = man.find(id);
				w.logout();
			}
		}
	}catch(Exception e){
		getContext().error(e,"Error in Wtmp logoff");
	}finally{
		removeAttribute(WTMP_EXPIRY_DATE);
		removeAttribute(WTMP_ID);
		removeAttribute(NAME_ATTR);
	}
}

public void logOut(){
	Integer super_person_id = (Integer) getAttribute(SUPER_PERSON_ID_ATTR);
	if( super_person_id != null){

		removeAttribute(SUPER_PERSON_ID_ATTR);

		A super_person = getLoginFactory().find(super_person_id);
		if( super_person != null){
			setCurrentPerson(super_person);
			resetNavigation();
			return;
		}
	}
	force_no_person=true;
	super.logOut();
	try{
		WtmpManager man = getWtmpManager();
		if( man != null ){
			Integer wtmp_id = getWtmpID();
			if( wtmp_id != null ){
				Wtmp w = man.find(wtmp_id);
				if( w != null){
					w.logout();
				}
			}
			setCookie(false, "");
		}
	}catch(Exception t){
		getContext().error(t, "Error closing Wtmp");
	}
	if( ss instanceof DefaultServletService){
		DefaultServletService defss = (DefaultServletService)ss;
		defss.logout(true);
	}
	// session is invalid may throw exception if accessed now
	use_session=false;
}
public A getSuperPerson(){
	Integer super_person_id = (Integer) getAttribute(SUPER_PERSON_ID_ATTR);
	if( super_person_id != null){
		return getLoginFactory().find(super_person_id);
	}
	return null;
}
@Override
public void setCurrentPerson(A person) {
	
	setCurrentPersonNoWtmp(person);
	if( use_session) {
		try {
			WtmpManager man = getWtmpManager();
			if( man != null ){


				Integer id = getWtmpID();
				if( id != null ){
					Wtmp w = man.find(id);
					w.logout();
				}

				if( request != null){
					AppUser real = getSuperPerson();
					Wtmp w = man.create(person, real,request);
					setCrossCookie(man, w);
					setAttribute(WTMP_ID, w.getID());
					setAttribute(WTMP_EXPIRY_DATE, w.getEndTime());
				}
			}
		} catch (DataException e) {
			getContext().error(e,"Error setting wtmp");
			removeAttribute(WTMP_EXPIRY_DATE);
			removeAttribute(WTMP_ID);
		}
		// Store name as an attribute.We don't use this
		// but it helps to identify users from the tomcat manager app.
		log_name = person.getName();
		A super_person = getSuperPerson();
		if( super_person != null) {
			log_name = log_name+" ("+super_person.getName()+")";
		}
		setNameHint( log_name);
		
		// Increase session timeout if configured
		int timeout = getContext().getIntegerParameter("session.logged_in.timeout", -1);
		if( timeout >= 0 ) {
			ss.setTimeout(timeout);
		}
		for(LoginObserver<A> o : getLoginFactory().getComposites(LoginObserver.class)) {
			o.userLoggedIn(person);
		}
	}
}
/** Set a name hint in the session.
 * This helps to identify users in the container admin/logging.
 * 
 * @param name
 */
public void setNameHint(String name) {
	setAttribute(NAME_ATTR, name);
}

private String getNameHint() {
	return (String) getAttribute(NAME_ATTR);
}
/**
 * @param person
 */
protected void setCurrentPersonNoWtmp(A person) {
	super.setCurrentPerson(person);
	force_no_person=false;
}

private String getCrossAppCookieName(){
	return getContext().getInitParameter("cross_cookie.cookie_name");
}
/**
 * @param man
 * @param w
 */
public void setCrossCookie(WtmpManager man, Wtmp w)  {
	try{
		String name = getCrossAppCookieName();
		if(name != null &&  (ss instanceof DefaultServletService)){
			CrossCookieComposite comp = man.getComposite(CrossCookieComposite.class);
			if( comp != null){
				String value = comp.getFullData(w);
				if( value != null ){
					setCookie(true,value);
				}
			}
		}
	}catch(Exception t){
		getContext().error(t,"Error setting cross app cookie");
	}
}

/**
 * @param value
 */
private void setCookie(boolean add,String value) {
	if( ! (ss instanceof DefaultServletService)){
		return;
	}
	String name = getCrossAppCookieName();
	if( name == null){
		return;
	}
	Cookie ck = new Cookie(name, value);
	ck.setHttpOnly(true);
	//ck.setSecure(true);
	if( add){
		ck.setMaxAge(-1);
	}else{
		ck.setMaxAge(0);
	}
	String domain = getContext().getInitParameter("cross_cookie.domain");
	if( domain != null){
		// Don't set cookies without a domain
		ck.setDomain(domain);
		ck.setPath("/");
		((DefaultServletService)ss).addCookie(ck);
	}
}

@Override
protected Integer getPersonID() {
	if( force_no_person ){
		return null;
	}
	Integer id = super.getPersonID();
	if( id != null ){
		if( log_name == null ) {
			// MAke sure we get the log name from the session at the point we
			// get the person from the session. session state may change during the request.
			log_name = getNameHint();
		}
		return id;
	}
	try{
		if( ! ss.isComitted() ){
			// suppress recursive lookup
			// no current person will be found until this block ends
			// This will allow us to check Preferences within the block
			// only makes sense for logging/debug preferences but these are still useful
			force_no_person=true;

			try {
				// We can't make a session once response is committed so
				// no point doing the lookup we can't store it.
				// We should not need to do person lookup after the fact
				ss.populateSession(this);
				id = super.getPersonID();
				if( id != null ){
					return id;
				}
				String name = getCrossAppCookieName();
				if( name != null){
					try{
						// look for a cross-login cookie
						Cookie[] cookies = request.getCookies();
						if( cookies != null){
							for(Cookie c : cookies){
								if( c.getName().equals(name) ){
									String value = c.getValue();
									WtmpManager man = getWtmpManager();
									if( man != null ){
										CrossCookieComposite ccs = man.getComposite(CrossCookieComposite.class);
										if( ccs != null){
											Wtmp w = man.find(ccs.getFilter(value),true);
											if( w != null){
												CurrentTimeService cts = getContext().getService(CurrentTimeService.class);
												Date now = cts.getCurrentTime();
												if( w.getEndTime().after(now)){
													AppUser person = w.getPerson();
													setCurrentPersonNoWtmp((A) person);
													setAttribute(WTMP_ID, w.getID());
													setAttribute(WTMP_EXPIRY_DATE, w.getEndTime());
													return person.getID();
												}
											}
										}
									}
								}
							}
						}
					}catch(Exception t){
						getContext().error(t,"Error reading cross app cookie");
					}
				}
			}finally {
				force_no_person=false;
			}

		}	
		// At one stage we explicitly stored 0 as the personid to prevent additional lookups.
		// This breaks the sign-up code as the newly created user is remembered as being
		// non-valid best to slow down unregistered users.
		clearCurrentPerson();
	}catch(Exception t){
		getContext().error(t, "Error populating session");
	}
	return 0;
}
public void su(A new_person){
	
	Integer current =  getPersonID();
		if( canSU(new_person)){
			
			Object super_person = getAttribute(SUPER_PERSON_ID_ATTR);
			// If already SU then just discard current identity
			if (super_person == null) {
					setAttribute(SUPER_PERSON_ID_ATTR, new Integer(current));
		    }
			setCurrentPerson(new_person);
			resetNavigation();
		}
}

private void resetNavigation() {
	NavigationMenuService nms = getContext().getService(NavigationMenuService.class);
	if( nms != null ){
		nms.resetMenu();
	}
}
public boolean canSU(A new_person) {
	if(hasRole(BECOME_USER_ROLE)){
		return true;
	}
	if( new_person ==  null ){
		return false;
	}
	// We re looking for a specific person Check for a sudo table
	PersonRelationship<A> pr = getPersonRelationship();
	if( pr != null ){
		return pr.hasRole(this.getCurrentPerson(),new_person, PersonRelationship.SUDO_ROLE);
	}
	return false;
}

public PersonRelationship<A> getPersonRelationship(){
	String sudo_table= getContext().getInitParameter(PERSON_RELATIONSHIP_TABLE);
	if( sudo_table != null ){
		return new PersonRelationship<>(getContext(), sudo_table);
	}
	return null;
}

public boolean isSU(){
	Object super_person = getAttribute(SUPER_PERSON_ID_ATTR);
	return super_person != null;
}

public void setUseSession(boolean use) {
	use_session=use;
}

@Override
protected boolean canLogin(A person) {
	return person.canLogin() || isSU();
}

@Override
public void addSecurityContext(Map att) {
	super.addSecurityContext(att);
	Integer wtmp = getWtmpID();
	if( wtmp != null ) {
		att.put(WTMP_ID, wtmp);
	}
}




}