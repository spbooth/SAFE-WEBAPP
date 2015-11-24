// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.session;

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import uk.ac.ed.epcc.webapp.AppContextService;
import uk.ac.ed.epcc.webapp.Contexed;
/** Service for managing session information.
 * This encodes all information about the current authenticated user, most importantly their
 * roles. The users may also have a database representation as an AppUser object which can also be 
 * stored in this service.
 * If no database representation of the users is required then the getLoginFactory should return null
 * but the getName and role methods can still be used.
 * 
 * 
 * @author spb
 * @param <A> type of AppUser
 *
 */
public interface SessionService<A extends AppUser> extends Contexed ,AppContextService<SessionService<A>>{
	/** Default administrator role.
	 * 
	 */
	public static final String ADMIN_ROLE="Admin";
	/** Get the Name for the current user.
	 * 
	 * This method can still be used when no login factory is configured.
	 * 
	 * @return String
	 */
	public String getName();
	/** Can we generate an AppUser for this session.
	 * 
	 * @return true if we know the current user
	 */
    public boolean haveCurrentUser();
    /** Does the current user have the specified role.
     * Always returns false if user is not known;
     * @param role
     * @return boolean
     */
	public boolean hasRole(String role);
	/** Get the set of toggle roles for the current user.
	 * These are roles that a user can enable/disable on request.
	 * 
	 * @return Set of role names.
	 */
	public Set<String> getToggleRoles();
	/** get the current State of a role toggle or null if not a toggle role
	 * 
	 * @param role
	 * @return Boolean or null
	 */
	public Boolean getToggle(String role);
	/** Set the toggle state of a role
	 * 
	 * @param name String role to set
	 * @param value boolean value to set
	 */
	public void setToggle(String name, boolean value);
	
	/** Toggle the sate of a role
	 * return the new value of the toggle or null if its not a togglable role
	 * 
	 * @param name String role to toggle
	 * @return Boolean or null
	 */
	public Boolean toggleRole(String name);
	/** Remember the current role_map belonging to the current user.
	 * 
	 * 
	 * @param toggle_map
	 */
	public void setCurrentRoleToggle(Map<String,Boolean> toggle_map);
	/** get the current person if known
	 * 
	 * @return AppUser or null
	 */
	public A getCurrentPerson();
	/** Set the current person
	 * 
	 * @param person
	 */
	public void setCurrentPerson(A person);
	/** Set the current person by id
	 * 
	 * @param id
	 */
	public void setCurrentPerson( int id);
	/** remove the current person
	 * 
	 */
	public void clearCurrentPerson();
	
	/** Clear current person and any saved state
	 * 
	 */
	public void logOut();
	
	/**
	 * get the concrete factory class for the AppUser used by this application
	 * 
	 * @return AppUserFactory
	 */
	public abstract  AppUserFactory<A> getLoginFactory();
	/** check for membership of any of the roles in list
	 * 
	 * @param role_list
	 * @return boolean
	 */
	public boolean hasRoleFromList(String ...role_list);
	/** request a role change for a specified user.
	 * This is an optional operation as a session service may not have the ability to modify roles.
	 * 
	 * @param user
	 * @param role
	 * @param value
	 * @throws UnsupportedOperationException
	 */
	public void setRole(A user, String role, boolean value) throws UnsupportedOperationException;
	/** Set a temporary (not stored to database) role.
	 * 
	 * @param role
	 */
	public void setTempRole(String role);
	
	/** query the default role set for the specified user.  
	 * This only queries the roles managed directly by the session service.
	 * This method should reflect the state set by setRole and should not be used to query 
	 * the current roles of the current user.
	 * No role mapping is applied
	 * @param user
	 * @param role
	 * @return is role permitted.
	 */
	public boolean canHaveRole(A user,String role);
	
	/** get the set of users with the specified role.
	 * This only queries the roles managed directly by the session service.
	 * This method should reflect the state set by setRole and should not be used to query 
	 * the current roles of the current user.
	 * @param role
	 * @return Set of AppUser
	 */
	public Set<A> withRole(String role);
	
	/** Store an object in the session. 
	 * Objects stored in the session should not contain references to the 
	 * AppContext as the AppCotnext might have a shorter lifetime.
	 * t
	 * 
	 * @param key
	 * @param value
	 */
	public void setAttribute(String key, Object value);
	/** remove object from session
	 * 
	 * @param key
	 */
	public void removeAttribute(String key);
	/** retrieve an object stored in the session.
	 * 
	 * @param key
	 * @return Object or null
	 */
	public Object getAttribute(String key);
	/** Get the Locale to use in the current context
	 * 
	 * @return Locale
	 */
	public Locale getLocale();
	/** Get the TimeZone to use in the current context.
	 * 
	 * @return TimeZone
	 */
	public TimeZone getTimeZone();
	/** Perform role-name mapping. This allows multiple
	 * specific roles to be mapped to a single meta-role.
	 * 
	 * @param role  specific role requests
	 * @return String actual role to use
	 */
	public String mapRoleName(String role);
}