package uk.ac.ed.epcc.webapp.model.period;

import uk.ac.ed.epcc.webapp.forms.transition.Transition;
import uk.ac.ed.epcc.webapp.session.SessionService;
/** A {@link Transition} that can supress the transition for certain objects.
 * 
 * @author spb
 *
 * @param <X> type of transition
 */
public interface GatedTransition<X> {

	boolean allow(SessionService<?> serv, X target);
}
