//| Copyright - The University of Edinburgh 2020                            |
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

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.jdbc.filter.AndFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.LinkManager;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.SessionService;
import uk.ac.ed.epcc.webapp.session.UnknownRelationshipException;

/** A base class for {@link LinkManager}s that implement {@link RelationshipProvider}.
 * 
 * 
 * @author Stephen Booth
 *
 * @param <A>
 * @param <B>
 * @param <L>
 */
public abstract class RelationshipLinkManager<A extends AppUser, B extends DataObject, L extends LinkManager.Link<A, B>>
		extends LinkManager<L, A, B> implements 
        RelationshipProvider<A, B> {

	protected RelationshipLinkManager(AppContext c,String tag,String person_field, DataObjectFactory<B> b_fac, String field) {
		super(c,tag,c.getService(SessionService.class).getLoginFactory(),person_field,b_fac,field);
	}
	
	

	public final BaseFilter<L> getUserRoleFilter(A user, String role) throws UnknownRelationshipException {
		return new LinkFilter(user, null, getFilterFromRole(role));
	}

	/** get a filter corresponding to the role.
	 * @param role
	 * @return
	 */
	protected abstract BaseFilter<L> getFilterFromRole(String role) throws UnknownRelationshipException;

	public final BaseFilter<L> getTargetRoleFilter(B target, String role) throws UnknownRelationshipException {
		LinkFilter fil = new LinkFilter(null,target, getFilterFromRole(role));
		if( target == null ){
			// if no target use factory default.
			fil.addFilter(getRightRemoteFilter(getRightFactory().getDefaultRelationshipFilter()));
		}
		return fil;
	}

	@SuppressWarnings("unchecked")
	public final BaseFilter<B> getTargetFilter(AppUser user, String role) throws UnknownRelationshipException {
		return getRightFilter(getUserRoleFilter((A) user,role));
	}

	public final BaseFilter<A> getUserFilter(B target, String role) throws UnknownRelationshipException {
		return getLeftFilter(getTargetRoleFilter(target, role));
	}

	public final BaseFilter<A> getUserFilter(BaseFilter<B> fil, String role) throws UnknownRelationshipException {
		AndFilter<L> and = new AndFilter<>(getTarget());
		and.addFilter(getFilterFromRole(role));
		and.addFilter(getRightRemoteFilter(fil));
		return getLeftFilter(and);
	}

	@Override
	public final DataObjectFactory<B> getTargetFactory() {
		return getRightFactory();
	}

	@Override
	public final BaseFilter<B> hasRelationFilter(String role, A user) {
		try {
			return getTargetFilter(user, role);
		} catch (UnknownRelationshipException e) {
			return null;
		}
	}

	@Override
	public final BaseFilter<A> personInRelationFilter(SessionService<A> sess, String role, B target) {
		try {
			return getUserFilter(target, role);
		} catch (UnknownRelationshipException e) {
			return null;
		}
	}

	@Override
	public final boolean providesRelationship(String role) {
		return getRelationships().contains(role);
	}



	@Override
	public final void addRelationships(Set<String> roles) {
		roles.addAll(getRelationships());
	}



	
}