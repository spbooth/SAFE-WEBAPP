// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.model.far;

import uk.ac.ed.epcc.webapp.model.data.transition.TransitionKey;
import uk.ac.ed.epcc.webapp.model.far.PartManager.Part;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** Key for transitions on {@link Part}s.
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.2 $")
public abstract class PartTransitionKey<T extends PartManager.Part> extends TransitionKey<T> {

	/**
	 * @param name
	 * @param help
	 */
	public PartTransitionKey( String name, String help) {
		super(PartManager.Part.class, name, help);
	
	}

	/**
	 * @param name
	 */
	public PartTransitionKey(String name) {
		super(PartManager.Part.class, name);
	}

	/** IS the operation allowed on the target;
	 * 
	 * @param target
	 * @param sess
	 * @return
	 */
	public abstract boolean allow(T target, SessionService<?> sess);
}