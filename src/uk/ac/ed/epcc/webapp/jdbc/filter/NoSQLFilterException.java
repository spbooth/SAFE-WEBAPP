// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp.jdbc.filter;


/** Exception that indicates a filter cannot be converted to a {@link SQLFilter}
 * 
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: NoSQLFilterException.java,v 1.3 2014/09/15 14:30:25 spb Exp $")

public class NoSQLFilterException extends CannotUseSQLException {

	public NoSQLFilterException() {
		super();
	}

	public NoSQLFilterException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoSQLFilterException(String message) {
		super(message);
	}

	public NoSQLFilterException(Throwable cause) {
		super(cause);
	}

}