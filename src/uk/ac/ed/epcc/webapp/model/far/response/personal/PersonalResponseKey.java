// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.model.far.response.personal;

import uk.ac.ed.epcc.webapp.model.data.transition.TransitionKey;
import uk.ac.ed.epcc.webapp.model.far.response.personal.PersonalResponseManager.PersonalResponse;

/**
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.1 $")
public class PersonalResponseKey<T extends PersonalResponse > extends TransitionKey<T> {

	/**
	 * @param t
	 * @param name
	 * @param help
	 */
	public PersonalResponseKey(String name, String help) {
		super(PersonalResponse.class, name, help);
	}

	/**
	 * @param t
	 * @param name
	 */
	public PersonalResponseKey(String name) {
		super(PersonalResponse.class, name);
	}

}
