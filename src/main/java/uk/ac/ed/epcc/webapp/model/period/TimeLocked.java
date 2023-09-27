package uk.ac.ed.epcc.webapp.model.period;

import uk.ac.ed.epcc.webapp.session.SessionService;

public interface TimeLocked<X> extends GatedTransition<X> {
	boolean allowTimeBounds(SessionService<?> serv, X target);
	@Override
	default boolean allow(SessionService<?> serv, X target) {
		return allowTimeBounds(serv, target);
	}
}
