package uk.ac.ed.epcc.webapp.session.perms;

import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.session.UnknownRelationshipException;

/** A {@link PermissionClause} is an Abstract Syntax Tree node from
 * a parse of a configuration statement from the permissions system
 * defining a relationship on a target object
 * 
 * @author Stephen Booth
 *
 * @param <T> Type of target object
 */
public interface PermissionClause<T extends DataObject>{

	<X> X accept(PermissionVisitor<X,T> visitor)throws UnknownRelationshipException;
}
