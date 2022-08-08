package uk.ac.ed.epcc.webapp.session;

import uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter;
import uk.ac.ed.epcc.webapp.model.data.Composite;

/** Interface for {@link Composite}s on an {@link AppUser}
 * that can veto is emails may be sent to the {@link AppUser}
 * 
 * @author Stephen Booth
 *
 */
public interface AllowedEmailContributor<AU extends AppUser> {

	/** get a filter for which {@link AppUser}s may be sent emails
	 * 
	 * @return
	 */
	public BaseFilter<AU> allowedEmailFilter();
	/** check a single {@link AppUser}
	 * 
	 * @param user
	 * @return
	 */
	public boolean allowEmail(AU user);
}
