package uk.ac.ed.epcc.webapp.session;

import java.util.Set;
/** Interface for objects (usually an {@link AppUserFactory} or {@link AppUserComposite}
 * that can provide information to help identify an email as genuine.
 * 
 * @author Stephen Booth
 *
 * @param <AU>
 */
public interface VerificationProvider<AU extends AppUser> {

	public void addVerifications(Set<String> verifications,AU person);
}
