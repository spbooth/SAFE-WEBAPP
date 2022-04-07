//| Copyright - The University of Edinburgh 2017                            |
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
package uk.ac.ed.epcc.webapp.model.data;

import java.util.Set;

import uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.FalseFilter;
import uk.ac.ed.epcc.webapp.model.data.Repository.FieldInfo;
import uk.ac.ed.epcc.webapp.model.data.convert.TypeProducer;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedProducer;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedTypeProducer;
import uk.ac.ed.epcc.webapp.model.relationship.AccessRoleProvider;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.SessionService;
import uk.ac.ed.epcc.webapp.session.UnknownRelationshipException;

/** A wrapper round a {@link DataObjectFactory} that forwards role relationships from a remote object
 * 
 * The AppUser has the role with the target object if the user has the same named role with
 * the remote object the target references.
 * 
 * @author spb
 * @param <U> Type of {@link AppUser}
 * @param <T> Type of hosting table
 * @param <R> Type of remote table
 *
 */
public class RemoteAccessRoleProvider<U extends AppUser,T extends DataObject,R extends DataObject> implements AccessRoleProvider<U, T> {

	public RemoteAccessRoleProvider(SessionService<U> sess,DataObjectFactory<T> home_fac, String link_field, boolean field_optional) throws UnknownRelationshipException {
		super();
		this.sess=sess;
		this.home_fac = home_fac;
		this.link_field = link_field;
		if( link_field == null || link_field.isEmpty()){
			throw new UnknownRelationshipException("Bad link field");
		}
		FieldInfo info = home_fac.res.getInfo(link_field);
		if( info == null ) {
			if( field_optional) {
				force=true;           // not an error but should never match
				remote_fac=null;
			}else {
				throw new UnknownRelationshipException("No reference field "+link_field+"@"+home_fac.getTag());
			}
		}else {
			force=false;

			if( ! info.isReference() ){
				throw new UnknownRelationshipException("Not a reference field "+link_field+"@"+home_fac.getTag());
			}
			TypeProducer prod = info.getTypeProducer();
			if( prod instanceof IndexedTypeProducer){
				IndexedProducer ip = ((IndexedTypeProducer)prod).getProducer();
				if( ip instanceof DataObjectFactory){
					remote_fac = (DataObjectFactory<R>) ip;
					return;
				}
			}
			throw new UnknownRelationshipException("Field "+link_field+" does not resolve to a DataObjectFactory");
		}
	}
	private final SessionService<U> sess;
	private final DataObjectFactory<T> home_fac;
	private final String link_field;
	private final DataObjectFactory<R> remote_fac;
	private final boolean force;
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.relationship.AccessRoleProvider#hasRelationFilter(java.lang.String, uk.ac.ed.epcc.webapp.session.AppUser)
	 */
	@Override
	public BaseFilter<T> hasRelationFilter(String role, U user) {
		if( role == null || role.isEmpty()){
			return null;
		}
		if( force ) {
			// Don't know type unless optional field exists
			// missing field should default to false
			return new FalseFilter<>((Class<T>)DataObject.class);
		}
		try {
			return home_fac.getRemoteFilter(remote_fac, link_field, sess.getTargetInRelationshipRoleFilter(remote_fac, role, user));
		} catch (UnknownRelationshipException e) {
			return null;
		}
	}
	
	@Override
	public BaseFilter<T> hasRelationFilter(String role, SessionService<U> sess) {
		if( role == null || role.isEmpty()){
			return null;
		}
		if( force ) {
			// Don't know type unless optional field exists
			// missing field should default to false
			return new FalseFilter<>((Class<T>)DataObject.class);
		}
		
		return home_fac.getRemoteFilter(remote_fac, link_field, sess.getRelationshipRoleFilter(remote_fac, role,null));
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.relationship.AccessRoleProvider#personInRelationFilter(uk.ac.ed.epcc.webapp.session.SessionService, java.lang.String, uk.ac.ed.epcc.webapp.model.data.DataObject)
	 */
	@Override
	public BaseFilter<U> personInRelationFilter(SessionService<U> sess, String role, T target) {
		if( role == null || role.isEmpty()){
			return null;
		}
		if( force ) {
			return new FalseFilter<>((Class<U>) AppUser.class);
		}
		if( target == null){
			try {
				return sess.getPersonInRelationshipRoleFilter(remote_fac, role, null);
			} catch (UnknownRelationshipException e) {
				return null;
			}
		}
		R remote = remote_fac.find(target.record.getNumberProperty(link_field));
		if( remote == null){
			return new FalseFilter<>((Class<U>) AppUser.class);
		}
		try {
			return sess.getPersonInRelationshipRoleFilter(remote_fac, role, remote);
		} catch (UnknownRelationshipException e) {
			return null;
		}
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.relationship.AccessRoleProvider#providesRelationship(java.lang.String)
	 */
	@Override
	public boolean providesRelationship(String role) {
		if( force ) {
			return false;
		}
		try {
			sess.getPersonInRelationshipRoleFilter(remote_fac, role, null);
			return true;
		} catch (UnknownRelationshipException e) {
			return false;
		}
		
	}
	@Override
	public void addRelationships(Set<String> roles) {
		
		
	}
	public DataObjectFactory<R> getRemoteFactory(){
		return remote_fac;
	}

}
