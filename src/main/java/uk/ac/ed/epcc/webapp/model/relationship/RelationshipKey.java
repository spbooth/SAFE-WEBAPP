package uk.ac.ed.epcc.webapp.model.relationship;

import uk.ac.ed.epcc.webapp.model.data.transition.TransitionKey;

public class RelationshipKey extends TransitionKey<Relationship.Link> {

	public RelationshipKey(String name, String help) {
		super(Relationship.Link.class, name, help);
	}

	public RelationshipKey(String name) {
		super(Relationship.Link.class, name);
	}

}
