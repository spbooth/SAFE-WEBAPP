package uk.ac.ed.epcc.webapp.model;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.model.data.transition.SimpleViewTransitionProvider;
import uk.ac.ed.epcc.webapp.session.SessionService;

public class ViewDummyTransitionProvider extends SimpleViewTransitionProvider<Dummy1, DummyKey> {
	public static final String DUMMY_TRANISTION_TAG = "Dummy";

	public ViewDummyTransitionProvider(AppContext conn) {
		super(conn,new Dummy1.Factory(conn),DUMMY_TRANISTION_TAG);
	}

	@Override
	public boolean canView(Dummy1 target, SessionService<?> sess) {
		return true;
	}

	@Override
	public boolean allowTransition(AppContext c, Dummy1 target, DummyKey key) {
		return true;
	}

	
}
