package uk.ac.ed.epcc.webapp.model.relationship;

import java.util.Set;

import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.Tagged;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.forms.RoleSelector;
import uk.ac.ed.epcc.webapp.session.AppUser;
/** Interface for classes that provide relationship roles between {@link AppUser}s
 * and a target object.
 * 
 * @author spb
 *
 * @param <A> AppUser type
 * @param <B> Target type
 */
public interface RelationshipProvider<A extends AppUser, B extends DataObject> extends RoleSelector<B>, Tagged, Contexed{

	public abstract SQLFilter<B> getTargetFilter(AppUser user, String role);

	public abstract boolean hasRole(A user, B target, String role);

	public abstract void setRole(A user, B target, String role, boolean value);

	
	public abstract Set<String> getRoles();

	
}