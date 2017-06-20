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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.PreRequisiteService;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.relationship.PersonRelationship;
import uk.ac.ed.epcc.webapp.servlet.CrossCookieComposite;
import uk.ac.ed.epcc.webapp.servlet.DefaultServletService;
import uk.ac.ed.epcc.webapp.servlet.ServletService;
import uk.ac.ed.epcc.webapp.servlet.WtmpManager;
import uk.ac.ed.epcc.webapp.servlet.WtmpManager.Wtmp;
import uk.ac.ed.epcc.webapp.servlet.navigation.NavigationMenuService;
import uk.ac.ed.epcc.webapp.session.AbstractSessionService;
import uk.ac.ed.epcc.webapp.session.AppUser;
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
	public static final String PERSON_RELATIONSHIP_TABLE = "person.relationship.table";

	public static final String BECOME_USER_ROLE = "BecomeUser";

	protected static final String SUPER_PERSON_ID_ATTR = "SuperPersonID";
	
	private static final String WTMP_ID = "SESSION_WTMP_ID";
	private static final String WTMP_EXPIRY_DATE = "SESSION_WTMP_EXPIRTY_DATE";
	private static final String NAME_ATTR="UserName";
	public static final Feature CROSS_APP_COOKIE_LOGIN_FEATURE = new Feature("cross_app_cookie",false,"Support cross app SSO via cookies");
	/**
	 * 
	 */
	private static final String WEBAPP_SESSION_COOKIE_NAME = "WEBAPP_SESSION";
	
	// Flag to supress re-populate if we logged out
	private boolean force_no_person=false;
	private ServletService ss;
	private HttpServletRequest request;
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
	  String name = super.getName();
	  AppContext c = getContext();
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
	if( ss instanceof DefaultServletService){
		DefaultServletService dss = (DefaultServletService)ss;
		// can't create session if comitted response
		sess = dss.getSession(! dss.isComitted());
	}
	if( value != null &&  ! (value instanceof Serializable)){
		LoggerService serv = getContext().getService(LoggerService.class);
		if( serv != null){
			serv.getLogger(getClass()).warn("Non serializable object "+key+" "+value.getClass().getCanonicalName()+" added to session");
		}
	}
	if( sess != null ){
		sess.setAttribute(key, value);
	}
	
}

public void removeAttribute(String key) {
	
	HttpSession sess = null;
	if( ss instanceof DefaultServletService){
		sess = ((DefaultServletService)ss).getSession();
	}
	if( sess != null ){
		sess.removeAttribute(key);
		
	}
}

public Object getAttribute(String key) {
	
	HttpSession sess = null;
	if( ss instanceof DefaultServletService){
		sess = ((DefaultServletService)ss).getSession();
	}
	if( sess != null ){
		return sess.getAttribute(key);
	}
	return null;
}
public WtmpManager getWtmpManager() {
	AppContext c = getContext();
	String table = c.getInitParameter("wtmp.table");
	if( table != null ){
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
					Date now = new Date();
					if( d.before(now)){
						Integer id = (Integer) getAttribute(WTMP_ID);
						if( id != null ){
							Wtmp w = man.find(id);
							w.update();
						}
					}
				}
			}catch(Throwable e){
				getContext().error(e, "Error updating Wtmp");
				removeAttribute(WTMP_EXPIRY_DATE);
				removeAttribute(WTMP_ID);
			}
		}

	}
	return user;
}

@Override
public void clearCurrentPerson() {
	
	super.clearCurrentPerson();
	force_no_person=true;
	try{
		WtmpManager man = getWtmpManager();
		if( man != null ){
			Integer id = (Integer) getAttribute(WTMP_ID);
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
	
	if( ss instanceof DefaultServletService){
		DefaultServletService defss = (DefaultServletService)ss;
		defss.logout(true);
	}
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
	
	super.setCurrentPerson(person);
	force_no_person=false;
	try {
		AppContext c = getContext();
		WtmpManager man = getWtmpManager();
		if( man != null ){


			Integer id = (Integer) getAttribute(WTMP_ID);
			if( id != null ){
				Wtmp w = man.find(id);
				w.logout();
			}
			if( request != null){
				Wtmp w = man.create(person, request);
				setCrossCookie(man, w);
				setAttribute(WTMP_ID, w.getID());
				setAttribute(WTMP_EXPIRY_DATE, w.getEndTime());
			}
		}
	} catch (DataException e) {
		c.error(e,"Error setting wtmp");
		removeAttribute(WTMP_EXPIRY_DATE);
		removeAttribute(WTMP_ID);
	}
	// Store name as an attribute.We don't use this
	// but it helps to identify users from the tomcat manager app.
	setAttribute(NAME_ATTR, person.getName());
}

/**
 * @param man
 * @param w
 * @throws DataFault
 */
public void setCrossCookie(WtmpManager man, Wtmp w)  {
	try{
		if(CROSS_APP_COOKIE_LOGIN_FEATURE.isEnabled(getContext()) &&  (ss instanceof DefaultServletService)){
			CrossCookieComposite comp = man.getComposite(CrossCookieComposite.class);
			if( comp != null){
				String value = comp.getFullData(w);
				if( value != null ){
					Cookie ck = new Cookie(WEBAPP_SESSION_COOKIE_NAME, value);
					//ck.setSecure(true);
					ck.setMaxAge(-1);
					String domain = getContext().getInitParameter("cross_cookie.domain");
					if( domain != null){
						// Don't set cookies without a domain
						ck.setDomain(domain);
						ck.setPath("/");
						((DefaultServletService)ss).addCookie(ck);
					}
				}
			}
		}
	}catch(Throwable t){
		getContext().error(t,"Error setting cross app cookie");
	}
}

@Override
protected Integer getPersonID() {
	if( force_no_person ){
		return null;
	}
	Integer id = super.getPersonID();
	if( id != null ){
		return id;
	}
	try{
		if( ! ss.isComitted() ){
			// We can't make a session once response is committed so
			// no point doing the lookup we can't store it.
			// We should not need to do person lookup after the fact
			ss.populateSession(this);
			id = super.getPersonID();
			if( id != null ){
				return id;
			}
			if( CROSS_APP_COOKIE_LOGIN_FEATURE.isEnabled(getContext())){
				try{
					// look for a cross-login cookie
					for(Cookie c : request.getCookies()){
						if( c.getName().equals(WEBAPP_SESSION_COOKIE_NAME) ){
							String value = c.getValue();
							WtmpManager man = getWtmpManager();
							if( man != null ){
								CrossCookieComposite ccs = man.getComposite(CrossCookieComposite.class);
								if( ccs != null){
									Wtmp w = man.find(ccs.getFilter(value),true);
									if( w != null){
										Date now = new Date();
										if( w.getEndTime().after(now)){
											AppUser person = w.getPerson();
											setCurrentPerson((A) person);
											setAttribute(WTMP_ID, w.getID());
											setAttribute(WTMP_EXPIRY_DATE, w.getEndTime());
											return person.getID();
										}
									}
								}
							}
						}
					}
				}catch(Throwable t){
					getContext().error(t,"Error reading cross app cookie");
				}
			}
		}	
		// At one stage we explicitly stored 0 as the personid to prevent additional lookups.
		// This breaks the sign-up code as the newly created user is remembered as being
		// non-valid best to slow down unregistered users.
		clearCurrentPerson();
	}catch(Throwable t){
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
		return pr.hasRole(this,new_person, PersonRelationship.SUDO_ROLE);
	}
	return false;
}

public PersonRelationship<A> getPersonRelationship(){
	String sudo_table= getContext().getInitParameter(PERSON_RELATIONSHIP_TABLE);
	if( sudo_table != null ){
		return new PersonRelationship<A>(getContext(), sudo_table);
	}
	return null;
}

public boolean isSU(){
	Object super_person = getAttribute(SUPER_PERSON_ID_ATTR);
	return super_person != null;
}

@Override
protected boolean canLogin(A person) {
	return person.canLogin() || isSU();
}



}