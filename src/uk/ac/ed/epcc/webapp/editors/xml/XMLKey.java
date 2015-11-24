package uk.ac.ed.epcc.webapp.editors.xml;

import uk.ac.ed.epcc.webapp.model.data.transition.TransitionKey;
import uk.ac.ed.epcc.webapp.session.SessionService;

public abstract class XMLKey extends TransitionKey<XMLTarget> {

	public XMLKey( String name, String help) {
		super(XMLTarget.class, name, help);
	}
	public XMLKey(String name){
		super(XMLTarget.class,name);
	}
	public abstract boolean allow(XMLTarget target, SessionService<?> sess);
}
