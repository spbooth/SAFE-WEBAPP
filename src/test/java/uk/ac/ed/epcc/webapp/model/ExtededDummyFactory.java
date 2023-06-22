package uk.ac.ed.epcc.webapp.model;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.model.Dummy1.Factory;
import uk.ac.ed.epcc.webapp.model.data.ConfigTag;

public class ExtededDummyFactory extends Factory {
	@ConfigTag("Extended")
	public static final String BOGUS="Bogus";
	
	public ExtededDummyFactory(AppContext c) {
		this(c,"ExtendedDummy1");
	
	}

	public ExtededDummyFactory(AppContext c, String table) {
		super(c, table);
	}

}
