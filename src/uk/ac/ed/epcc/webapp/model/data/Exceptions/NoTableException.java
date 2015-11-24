package uk.ac.ed.epcc.webapp.model.data.Exceptions;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;

/** Exception thrown when the specified table does not exist yet
 * 
 * @author spb
 *
 */
public class NoTableException extends DataException {

	public NoTableException(String str) {
		super(str);
	}

	public NoTableException(String str, Throwable cause) {
		super(str, cause);
	}

}
