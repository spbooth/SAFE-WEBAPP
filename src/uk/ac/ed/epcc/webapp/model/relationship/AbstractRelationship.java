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
package uk.ac.ed.epcc.webapp.model.relationship;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.AndFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.LinkManager;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.forms.RoleSelector;
import uk.ac.ed.epcc.webapp.model.data.forms.inputs.DataObjectItemInput;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.SessionService;
import uk.ac.ed.epcc.webapp.session.UnknownRelationshipException;


/** abstract Link class that encodes a relationship between an AppUser and a
 * domain object and used to implement {@link RelationshipProvider} and {@link RoleSelector} for the domain object.
 *
 * How roles are actually encoded into the link object is deferred to the sub-class.
 * 
 * 
 * @author spb
 *
 * @param <A> {@link AppUser} type
 * @param <B> target type
 * @param <L> Link type
 */


public abstract class AbstractRelationship<A extends AppUser,B extends DataObject, L extends AbstractRelationship.Link<A, B>> extends 
         LinkManager<L,A,B> implements 
         RelationshipProvider<A, B>, RoleSelector<B>{
    
	
	
	/** Extension constructor to allow sub-classes to set factory and field
	 * 
	 */
	@SuppressWarnings("unchecked")
	protected AbstractRelationship(AppContext c,String tag,String person_field, DataObjectFactory<B> b_fac, String field) {
		super(c,tag,c.getService(SessionService.class).getLoginFactory(),person_field,b_fac,field);
	}
	
    
   
   

	public abstract static class Link<A extends AppUser,B extends DataObject> extends LinkManager.Link<A,B>{

		protected Link(AbstractRelationship< A, B,?> arg0, Record arg1) {
			super(arg0, arg1);
		}

		@Override
		protected void setup() throws DataFault, DataException {
		}
		public abstract boolean hasRole(String role);
		public abstract void setRole(String role, boolean value);
    	
    }

	
	public final BaseFilter<L> getUserRoleFilter(A user,String role) throws UnknownRelationshipException{
		return new LinkFilter(user, null, getFilterFromRole(role));
	}

	/** get a filter corresponding to the role.
	 * @param role
	 * @return
	 */
	protected abstract BaseFilter<L> getFilterFromRole(String role) throws UnknownRelationshipException;
	
	
	public final BaseFilter<L> getTargetRoleFilter(B target,String role) throws UnknownRelationshipException{
		LinkFilter fil = new LinkFilter(null,target, getFilterFromRole(role));
		if( target == null ){
			// if no target use factory default.
			fil.addFilter(getRightRemoteFilter(getRightFactory().getDefaultRelationshipFilter()));
		}
		return fil;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.safe.accounting.db.RelationshipProvider#getTargetFilter(uk.ac.ed.epcc.webapp.session.AppUser, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public final BaseFilter<B> getTargetFilter(AppUser user,String role) throws UnknownRelationshipException{
		return getRightFilter(getUserRoleFilter((A) user,role));
	}
	
	
	public final BaseFilter<A> getUserFilter(B target,String role) throws UnknownRelationshipException{
		return getLeftFilter(getTargetRoleFilter(target, role));
	}
    public final BaseFilter<A> getUserFilter(BaseFilter<B> fil,String role) throws UnknownRelationshipException{
    	AndFilter<L> and = new AndFilter<>(getTarget());
    	and.addFilter(getFilterFromRole(role));
    	and.addFilter(getRightRemoteFilter(fil));
    	return getLeftFilter(and);
    }
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.safe.accounting.db.RelationshipProvider#getInput(java.lang.String, uk.ac.ed.epcc.webapp.session.SessionService)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public final DataObjectItemInput<B> getInput(String role, SessionService user) {
		return getRightFactory().getInput(hasRelationFilter(role,(A)user.getCurrentPerson()));
	}
	

	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.safe.accounting.db.RelationshipProvider#hasRole(A, B, java.lang.String)
	 */
	public final boolean hasRole(A user, B target, String role){
		if( ! getLeftFactory().isMine(user) || ! getRightFactory().isMine(target)){
			return false;
		}
		try{
			L link = getLink(user, target);
			if( link != null && link.hasRole(role)){
				return true;
			}
			return false;
		}catch(Exception e){
			getLogger().error("Error getting role",e);
			return false;
		}
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.safe.accounting.db.RelationshipProvider#setRole(A, B, java.lang.String, boolean)
	 */
	public final void setRole(A user, B target, String role, boolean value){
		if( ! getLeftFactory().isMine(user) || ! getRightFactory().isMine(target)){
			throw new ConsistencyError("Factory types do not match");
		}
		if( ! getRelationships().contains(role)){
			getLogger().error("Invalid role "+role);
			return;
		}
		try{
			L link = makeLink(user, target);
			link.setRole(role, value);
			link.commit();
		}catch(Exception e){
			getLogger().error("Error making role",e);
		}
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.safe.accounting.db.RelationshipProvider#hasRole(int, java.lang.String)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public final boolean hasRole(SessionService sess,B target,String role){
		return hasRole((A) sess.getCurrentPerson(),target ,role);
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.IndexedLinkManager#selectLink(uk.ac.ed.epcc.webapp.model.data.Indexed, uk.ac.ed.epcc.webapp.model.data.Indexed)
	 */
	@Override
	protected final L selectLink(A leftEnd, B rightEnd) throws Exception {
		return makeLink(leftEnd, rightEnd);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.safe.accounting.db.RelationshipProvider#hasRole(java.lang.String, uk.ac.ed.epcc.webapp.session.SessionService)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public final boolean hasRole(String role, SessionService user) {
		A u = (A) user.getCurrentPerson();
		if( u == null){
			return false;
		}
		try {
			return exists(getUserRoleFilter(u, role));
		} catch (Exception e) {
			getLogger().error("Error checking match",e);
			return false;
		}
	}
	@Override
	public final boolean canCreate(SessionService c){
		// link objects are created from the update form
		return false;
	}

	@Override
	public final DataObjectFactory<B> getTargetFactory() {
		return getRightFactory();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.relationship.AccessRoleProvider#hasRelationFilter(uk.ac.ed.epcc.webapp.session.SessionService, java.lang.String)
	 */
	@Override
	public final BaseFilter<B> hasRelationFilter(String role, A user) {
		try {
			return getTargetFilter(user, role);
		} catch (UnknownRelationshipException e) {
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.relationship.AccessRoleProvider#personInRelationFilter(uk.ac.ed.epcc.webapp.session.SessionService, java.lang.String, uk.ac.ed.epcc.webapp.model.data.DataObject)
	 */
	@Override
	public final BaseFilter<A> personInRelationFilter(SessionService<A> sess, String role, B target) {
		try {
			return getUserFilter(target, role);
		} catch (UnknownRelationshipException e) {
			return null;
		}
	}
	@Override
	public final  boolean providesRelationship(String role) {
		return getRelationships().contains(role);
	}

}