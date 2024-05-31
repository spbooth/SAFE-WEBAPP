package uk.ac.ed.epcc.webapp.model;

import uk.ac.ed.epcc.webapp.model.data.transition.TransitionKey;

public class DummyKey extends TransitionKey<Dummy1> {

	public DummyKey(String name, String help) {
		super(Dummy1.class, name, help);
	}

	public DummyKey(String name) {
		super(Dummy1.class, name);
	}

}
