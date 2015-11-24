// Copyright - The University of Edinburgh 2012
package uk.ac.ed.epcc.webapp.jdbc.expr;

/** Exception thrown on an illegal combine operation.
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: CombineException.java,v 1.2 2014/09/15 14:30:23 spb Exp $")
public class CombineException extends Exception{

	/**
	 * 
	 */
	public CombineException(String mess) {
		super(mess);
	}

}
