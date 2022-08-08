package uk.ac.ed.epcc.webapp.session;

public interface LoginObserver<A extends AppUser> {
	public void userLoggedIn(A user);
}
