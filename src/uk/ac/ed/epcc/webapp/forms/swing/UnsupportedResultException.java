// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp.forms.swing;
/** Exception thrown for FormResults that cannot be processed by the handler
 * 
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: UnsupportedResultException.java,v 1.2 2014/09/15 14:30:22 spb Exp $")

public class UnsupportedResultException extends Exception {

	public UnsupportedResultException() {
		super();
	}

	public UnsupportedResultException(String arg0) {
		super(arg0);
	}

}