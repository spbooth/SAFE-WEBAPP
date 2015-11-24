// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.model.far;

import uk.ac.ed.epcc.webapp.model.data.transition.TransitionKey;
import uk.ac.ed.epcc.webapp.model.far.DynamicFormManager.DynamicForm;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** A {@link TransitionKey} used by {@link DynamicFormTransitionProvider}
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.2 $")
public abstract class DynamicFormTransitionKey<T extends DynamicForm> extends TransitionKey<T> {

	/**
	 * @param name
	 * @param help
	 */
	public DynamicFormTransitionKey( String name, String help) {
		super(DynamicForm.class, name, help);
	
	}

	/**
	 * @param name
	 */
	public DynamicFormTransitionKey(String name) {
		super(DynamicForm.class, name);
	}

	/** IS the operation allowed on the target;
	 * 
	 * @param target
	 * @param sess
	 * @return
	 */
	public abstract boolean allow(DynamicForm target, SessionService<?> sess);
}