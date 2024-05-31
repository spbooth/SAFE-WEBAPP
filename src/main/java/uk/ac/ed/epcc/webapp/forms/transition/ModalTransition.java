package uk.ac.ed.epcc.webapp.forms.transition;

import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;

/** A transition that can either use a form or act directly based on the state of the target
 * 
 * @param <X>
 */
public interface ModalTransition<X> extends FormTransition<X>, DirectTransition<X> {
	public boolean useDirect(X target);

	@Override
	default FormResult getResult(TransitionVisitor<X> vis) throws TransitionException {
		return vis.doModalTransition(this);
	}
}
