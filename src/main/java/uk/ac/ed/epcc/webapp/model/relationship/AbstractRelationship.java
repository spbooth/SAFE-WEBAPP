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
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.LinkManager;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.SessionService;


/** abstract Link class that encodes an editable relationship between an AppUser and a
 * domain object and used to implement {@link RelationshipProvider} for the domain object.
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
         RelationshipLinkManager<A, B, L> {
    
	
	
	/** Extension constructor to allow sub-classes to set factory and field
	 * 
	 */
	protected AbstractRelationship(AppContext c,String tag,String person_field, DataObjectFactory<B> b_fac, String field) {
		super(c,tag,person_field,b_fac,field);
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
	 * @see uk.ac.ed.epcc.webapp.model.data.IndexedLinkManager#selectLink(uk.ac.ed.epcc.webapp.model.data.Indexed, uk.ac.ed.epcc.webapp.model.data.Indexed)
	 */
	@Override
	protected final L selectLink(A leftEnd, B rightEnd) throws Exception {
		return makeLink(leftEnd, rightEnd);
	}
	
	@Override
	public final boolean canCreate(SessionService c){
		// link objects are created from the update form
		return false;
	}

}