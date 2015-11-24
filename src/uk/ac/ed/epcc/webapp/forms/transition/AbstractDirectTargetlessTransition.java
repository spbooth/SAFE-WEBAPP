// Copyright - The University of Edinburgh 2014
package uk.ac.ed.epcc.webapp.forms.transition;

import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;

/** A simple abstract sub-class for {@link DirectTargetlessTransition}s
 * 
 * This saves a small amount of boiler plate when no other superclass is required.
 * @author spb
 * @param <X> 
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.1 $")
public abstract class AbstractDirectTargetlessTransition<X> implements
		DirectTargetlessTransition<X> {

	public final FormResult getResult(TransitionVisitor<X> vis)
			throws TransitionException {
		return vis.doDirectTargetlessTransition(this);
	}


}
