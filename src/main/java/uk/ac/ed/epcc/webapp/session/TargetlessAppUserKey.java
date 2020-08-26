package uk.ac.ed.epcc.webapp.session;

public class TargetlessAppUserKey extends AppUserKey {

	
	private final String role;
	public TargetlessAppUserKey(String name, String help, String role) {
		super(name, help);
		this.role=role;
	}
	public TargetlessAppUserKey(String name,String text, String help, String role) {
		super(name, text,help);
		this.role=role;
	}
	

	@Override
	public final boolean allow(AppUser user, SessionService op) {
			return user == null && op.hasRole(role);
	}

	
}
