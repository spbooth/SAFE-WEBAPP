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
package uk.ac.ed.epcc.webapp.session;

import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.function.Supplier;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.AppContextService;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
/** {@link AppContextService} for managing session information.
 * This encodes all information about the current authenticated user, most importantly their
 * roles. The users may also have a database representation as an {@link AppUser} object which can also be 
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
	
	/** enable/disable the toggle checks for this session
	 * 
	 * @param value
	 */
	public void setApplyToggle(boolean value);
	
	/** are toggle roles currently enabled
	 * 
	 * @return
	 */
	public boolean getApplyToggle();
	/** Toggle the state of a role
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
	
	/** does the current session correspond to the person.
	 * 
	 * @param person
	 * @return boolean 
	 */
	public boolean isCurrentPerson(A person);
	/** Set the current person by id
	 * 
	 * @param id
	 */
	public void setCurrentPerson( int id);
	/** remove the current person
	 * 
	 */
	public void clearCurrentPerson();
	
	/** If there is a current user return the time they authenticated
	 * This should only record a user-present authentication not API access via a token
	 * @return
	 */
	public Date getAuthenticationTime();
	/** Set the authentication time
	 * 
	 * @param d
	 */
	public void setAuthenticationTime(Date d);
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
	
	default public boolean hasRoleFromList(String ...roles){
		if( roles == null ){
			return false;
		}
		for(String role : roles){
			if( hasRole(role)){
				return true;
			}
		}
		return false;
	}
	
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
	
	
	default public boolean canHaveRoleFromList(A user,String ... roles) {
		if( roles == null ) {
			return false;
		}
		for(String role : roles) {
			if( canHaveRole(user, role)) {
				return true;
			}
		}
		return false;
	}
	
//	/** get the set of users with the specified role.
//	 * This only queries the roles managed directly by the session service.
//	 * This method should reflect the state set by setRole and should not be used to query 
//	 * the current roles of the current user.
//	 * @param role
//	 * @return Set of AppUser
//	 */
//	public Set<A> withRole(String role);
	
	/** Store an object in the session. 
	 * Objects stored in the session should not contain references to the 
	 * AppContext as the {@link AppContext} might have a shorter lifetime.
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
	
	/** Get the set of standard roles.
	 * Used by add-role forms etc.
	 * 
	 * @return
	 */
	public Set<String> getStandardRoles();
	public TimeZone getTimeZone();
	/** Perform role-name mapping. This allows multiple
	 * specific roles to be mapped to a single meta-role.
	 * 
	 * @param role  specific role requests
	 * @return String actual role to use
	 */
	public String mapRoleName(String role);
	
	/** get a {@link BaseFilter} representing the set of target objects that the current user has
	 * a particular relationship-role with.
	 * 
	 * @param fac {@link DataObjectFactory} for target object
	 * @param role 
	 * @return {@link BaseFilter}
	 */
	public <T extends DataObject> BaseFilter<T> getRelationshipRoleFilter(DataObjectFactory<T> fac, String role) throws UnknownRelationshipException;
	
	/** get a {@link BaseFilter} representing the set of target objects that the current user has
	 * a particular relationship-role with.
	 * 
	 * If the named relationship is defined it is used to narrow the selection of the fallback filter.
	 * Otherwise just the fallback filter is returned. 
	 * 
	 * @param fac {@link DataObjectFactory} for target object
	 * @param role 
	 * @param fallback {@link BaseFilter} to use by default.
	 * @return {@link BaseFilter}
	 */
	public <T extends DataObject> BaseFilter<T> getRelationshipRoleFilter(DataObjectFactory<T> fac, String role,BaseFilter<T> fallback);

	/** get a {@link BaseFilter} representing the set of {@link AppUser}s that are in a particular
	 * relationship-role with a target object.
	 * A null target selects all {@link AppUser}s that have the specified role with any target matched by the
	 * factories default  {@link DataObjectFactory#getDefaultRelationshipFilter()}.
	 * @param fac
	 * @param role
	 * @param target
	 * @return {@link BaseFilter}
	 */
	public <T extends DataObject> BaseFilter<A> getPersonInRelationshipRoleFilter(DataObjectFactory<T> fac, String role,T target) throws UnknownRelationshipException;
	
	/** Get a filter for {@link AppUser}s that can be in any of the specified global roles.
	 * as defined in {@link #canHaveRoleFromList(AppUser, String...)}
	 * 
	 * @param role_list
	 * @return
	 */
	public BaseFilter<A> getPersonInRoleFilter(String ... role_list);
	/** get a {@link BaseFilter} representing the set of targets that a specified {@link AppUser} is in a particular
	 * relationship-role with.
	 * @param fac
	 * @param role
	 * @param person
	 * @return {@link BaseFilter}
	 */
	public <T extends DataObject> BaseFilter<T> getTargetInRelationshipRoleFilter(DataObjectFactory<T> fac, String role,A person) throws UnknownRelationshipException;
	/** Method to check relationships on a specified target object.
	 * 
	 * Note that {@link #getRelationshipRoleFilter(DataObjectFactory, String)} is sufficient for this
	 * in combination with {@link DataObjectFactory#matches(BaseFilter, DataObject)} but
	 * adding a method to {@link SessionService} reduces code duplication and adds to possibility of caching the results.
	 * 
	 * 
	 * @param fac     {@link DataObjectFactory}
	 * @param target  {@link DataObject} to test for relationship
	 * @param role    String role to test
	 * @return  boolean true if has relationship
	 * @throws UnknownRelationshipException 
	 */
	public <T extends DataObject> boolean hasRelationship(DataObjectFactory<T> fac, T target,String role) throws UnknownRelationshipException;
	/** Method to check relationships on a specified target object.
	 * 
	 * 
	 * @param fac     {@link DataObjectFactory}
	 * @param target  {@link DataObject} to test for relationship
	 * @param role    String role to test
	 * @param fallback boolean value to use if relationship undefined
	 * @return  boolean true if has relationship
	 */
	public default <T extends DataObject> boolean hasRelationship(DataObjectFactory<T> fac, T target,String role,boolean fallback) {
		return hasRelationship(fac, target, role, () -> fallback);
	};
	
	/** Method to check relationships on a specified target object.
	 * 
	 * 
	 * @param fac     {@link DataObjectFactory}
	 * @param target  {@link DataObject} to test for relationship
	 * @param role    String role to test
	 * @param fallback {@link Supplier} value to use if relationship undefined
	 * @return  boolean true if has relationship
	 */
	public <T extends DataObject> boolean hasRelationship(DataObjectFactory<T> fac, T target,String role,Supplier<Boolean> fallback);
	
	/** Convenience method to query the relationships of a specified person rather than
	 * the current user
	 * Note this will not consider global roles unless the person matches the current person
	 * @param <T>
	 * @param person
	 * @param fac
	 * @param target
	 * @param role
	 * @param fallback
	 * @return
	 */
	public default <T extends DataObject> boolean personHasRelationship(A person,DataObjectFactory<T> fac, T target,String role,Supplier<Boolean> fallback ) {
		try {
			if( isCurrentPerson(person)) {
				return hasRelationship(fac, target, role, fallback);
			}
			return getLoginFactory().matches(getPersonInRelationshipRoleFilter(fac, role, target), person);
		} catch (UnknownRelationshipException e) {
			return fallback.get();
		}
	}
	/** Tests if the session has already authenticated. This can return false even if {@link #haveCurrentUser()} could return true
	 * provided it is called first.
	 * 
	 * 
	 * 
	 */
	public boolean isAuthenticated();
}