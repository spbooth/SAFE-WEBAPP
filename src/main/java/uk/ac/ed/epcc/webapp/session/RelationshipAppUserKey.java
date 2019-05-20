package uk.ac.ed.epcc.webapp.session;

public class RelationshipAppUserKey<AU extends AppUser> extends AppUserKey<AU> {

	
	private final String role;
	public RelationshipAppUserKey(String name, String help, String role) {
		super(name, help);
		this.role=role;
	}
	public RelationshipAppUserKey(String name,String text, String help, String role) {
		super(name, text,help);
		this.role=role;
	}
	

	@Override
	public final boolean allow(AU user, SessionService op) {
		try {
			return user != null && allowState(user) && op.hasRelationship(op.getLoginFactory(), user, role);
		} catch (UnknownRelationshipException e) {
			getLogger(op.getContext()).error("Error checking permission "+role,e);
			return false;
		}
	}

	protected boolean allowState(AU user) {
		return true;
	}
}
