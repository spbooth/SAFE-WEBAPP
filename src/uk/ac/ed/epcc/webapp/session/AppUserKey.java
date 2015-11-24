// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.session;

import uk.ac.ed.epcc.webapp.model.data.transition.TransitionKey;

/**
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.2 $")
public class AppUserKey<T extends AppUser> extends TransitionKey<T> {

	/**
	 * @param t
	 * @param name
	 * @param help
	 */
	public AppUserKey(Class<? super T> t, String name, String help) {
		super(t, name, help);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param t
	 * @param name
	 */
	public AppUserKey(Class<? super T> t, String name) {
		super(t, name);
		// TODO Auto-generated constructor stub
	}

}
