//| Copyright - The University of Edinburgh 2016                            |
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
package uk.ac.ed.epcc.webapp.model.relationship;

import java.util.Set;

import uk.ac.ed.epcc.webapp.jdbc.filter.*;
import uk.ac.ed.epcc.webapp.model.data.Composite;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.NamedFilterProvider;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.SessionService;

/**  Interface for classes that provide relationship roles between an {@link AppUser}
 * and a target object.
 * 
 * If the relationship only depends on the target object not on the {@link AppUser} then
 * use {@link NamedFilterProvider}.
 * 
 * {@link DataObjectFactory}s and {@link Composite}s should implement this interface for roles provided by
 * {@link AppUser} references. In this case it is a good idea to return a {@link DualFilter} to allow tests
 * without accessing the database.
 * 
 * It can also be implemented by stand-alone plug-ins. 
 * 
 * @see RelationshipProvider
 * @see NamedFilterProvider
 * @see SessionService
 * @author spb
 * @param <U> AppUser type
 * @param <T> Target type
 *
 */
public interface AccessRoleProvider<U extends AppUser,T extends DataObject> {
	/** Get a {@link BaseFilter} corresponding to target objects where the given user has
	 * the specified relation.
	 * If the method returns null then the role is not recognised by the provider. 
	 * A non-null result means the role is recognised but does not imply
	 * that any target will match. The relation with a specific target object can be tested using
	 * {@link DataObjectFactory#matches(BaseFilter, DataObject)}
	 * 
	 * This method should not be called directly only via a call to {@link SessionService#getRelationshipRoleFilter(DataObjectFactory, String)}
	 * to allow the {@link SessionService} to combine and customise access rules.
	 * 
	 * @param role
	 * @param user 
	 * @return {@link BaseFilter} or null
	 */
	public BaseFilter<T> hasRelationFilter(String role, U user);
	/**Get a {@link BaseFilter} corresponding to target objects where the current session has
	 * the specified relation.
	 * If the method returns null then the role is not recognised by the provider. 
	 * A non-null result means the role is recognised but does not imply
	 * that any target will match. The relation with a specific target object can be tested using
	 * {@link DataObjectFactory#matches(BaseFilter, DataObject)}
	 * 
	 * This method should not be called directly only via a call to {@link SessionService#getRelationshipRoleFilter(DataObjectFactory, String)}
	 * to allow the {@link SessionService} to combine and customise access rules.
	 * 
	 * 
	 * @param role
	 * @param sess
	 * @return
	 */
	public default BaseFilter<T> hasRelationFilter(String role, SessionService<U> sess){
		return hasRelationFilter(role, sess.getCurrentPerson());
	}
	
	/** Get a {@link BaseFilter} for {@link AppUser}s that are in the specified relationship with
	 * the target object.
	 * 
	 * This is the inverse of {@link #hasRelationFilter(String, AppUser)} used to generate a list
	 * of {@link AppUser} with the relation. It can always be implemented (inefficiently) by creating an {@link AcceptFilter}
	 * that uses {@link #hasRelationFilter(String, AppUser)} to check each person in turn but though it is usually possible to find some {@link SQLFilter}
	 * to narrow the selection first even if a full SQL implementation is not possible.
	 * 
	 * If the target is null it should generate a filter for any user in relation with targets
	 * selected by {@link DataObjectFactory#getDefaultRelationshipFilter()}. If this is not possible
	 * it should return null;
	 * @return {@link BaseFilter} or null
	 */
    public BaseFilter<U> personInRelationFilter(SessionService<U> sess, String role, T target);
    
    /**
     *  Get a {@link SQLFilter} for {@link AppUser}s that have a specified relationship
     *  with any target that matches a {@link SQLFilter}
     *  
     *  This is largely an optimisation for SQLfilters and will not be possible unless the
     *  role is implemented as a {@link SQLFilter}. Non SQL cases can always loop over {@link AppUser}s
     *  or target objects. 
     *  
     * @param sess
     * @param role
     * @param fil
     * @return
     * @throws CannotUseSQLException
     */
    default public SQLFilter<U> personInRelationToFilter(SessionService<U> sess, String role, SQLFilter<T> fil) throws CannotUseSQLException{
    	if( providesRelationship(role)) {
    		throw new NoSQLFilterException("personInRelationToFilter not implemented in "+getClass().getCanonicalName());
    	}
    	return null;
    }
    
    /** Does this class provide the named relationship.
     * 
     * 
     * 
     * @param role
     * @return true if relationship provided by this class.
     */
    public boolean providesRelationship(String role);
    
    /** Add known roles to a set. This can be roles provided or consumed
     * by the class. This is only used as auto-complete suggestions so
     * should be a useful subset rather than an exhaustive list
     * 
     * @param roles
     */
    public void addRelationships(Set<String> roles);
}
