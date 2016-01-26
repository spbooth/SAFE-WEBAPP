// Copyright - The University of Edinburgh 2016
package uk.ac.ed.epcc.webapp.servlet.thread;

import uk.ac.ed.epcc.webapp.AppContext;

/** A duplicate {@link AddDataTransitionProvider}
 * used in tests to bypass the provider lock 
 * @author spb
 *
 */
public class AddDataTransitionProvider2 extends AddDataTransitionProvider {

	/**
	 * @param c
	 * @param target_name
	 */
	public AddDataTransitionProvider2(AppContext c, String target_name) {
		super(c, target_name);
	}

}
