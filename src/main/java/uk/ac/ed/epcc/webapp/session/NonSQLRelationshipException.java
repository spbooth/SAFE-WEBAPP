package uk.ac.ed.epcc.webapp.session;

import uk.ac.ed.epcc.webapp.jdbc.filter.CannotUseSQLException;

/** A {@link UnknownRelationshipException} where the relationship is known but
 * the requested operation cannot be implemented in SQL
 * 
 * @author Stephen Booth
 *
 */
public class NonSQLRelationshipException extends UnknownRelationshipException {

	public NonSQLRelationshipException(String msg) {
		super(msg);
		
	}

	public NonSQLRelationshipException(String msg,CannotUseSQLException nested) {
		super(msg, nested);
		
	}

}
