package uk.ac.ed.epcc.webapp.session;

import uk.ac.ed.epcc.webapp.servlet.ViewTransitionKey;

public class BookmarkableRelationshipAppUserKey<AU extends AppUser> extends RelationshipAppUserKey<AU> implements ViewTransitionKey<AU> {

	public BookmarkableRelationshipAppUserKey(String name, String text, String help, String role) {
		super(name, text, help, role);
	}

	public BookmarkableRelationshipAppUserKey(String name, String help, String role) {
		super(name, help, role);
	}

}
