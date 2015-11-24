// Copyright - The University of Edinburgh 2014
package uk.ac.ed.epcc.webapp.forms.transition;

import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;

/** A simple abstract sub-class for {@link TargetlessTransition}s
 * 
 * This saves a small amount of boiler plate when no other superclass is required.
 * @author spb
 * @param <X> 
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.1 $")
public abstract class AbstractTargetLessTransition<X> implements TargetLessTransition<X> {

	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.Transition#getResult(uk.ac.ed.epcc.webapp.forms.transition.TransitionVisitor)
	 */
	public final FormResult getResult(TransitionVisitor<X> vis)
			throws TransitionException {
		return vis.doTargetLessTransition(this);
	}

}
