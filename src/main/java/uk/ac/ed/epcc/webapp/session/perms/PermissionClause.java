package uk.ac.ed.epcc.webapp.session.perms;

import uk.ac.ed.epcc.webapp.Targetted;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.session.UnknownRelationshipException;

public interface PermissionClause<T extends DataObject> extends Targetted<T>{

	<X> X accept(PermissionVisitor<X,T> visitor)throws UnknownRelationshipException;
}
