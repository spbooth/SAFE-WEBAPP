package uk.ac.ed.epcc.webapp.session;

public class RoleAppUserKey extends AppUserKey {

	
	private final String role;
	public RoleAppUserKey(String name, String help, String role) {
		super(name, help);
		this.role=role;
	}
	public RoleAppUserKey(String name,String text, String help, String role) {
		super(name, text,help);
		this.role=role;
	}
	

	@Override
	public final boolean allow(AppUser user, SessionService op) {
			return user != null && allowState(user) && op.hasRole(role);
	}

	protected boolean allowState(AppUser user) {
		return true;
	}
}
