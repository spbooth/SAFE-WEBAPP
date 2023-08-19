package uk.ac.ed.epcc.webapp.model;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.model.Dummy1.Factory;

public class ExtededDummyFactory extends Factory {
	
	
	public ExtededDummyFactory(AppContext c) {
		this(c,"ExtendedDummy1");
	
	}

	public ExtededDummyFactory(AppContext c, String table) {
		super(c, table);
	}

}
