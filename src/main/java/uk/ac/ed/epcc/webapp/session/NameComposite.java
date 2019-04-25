package uk.ac.ed.epcc.webapp.session;

import uk.ac.ed.epcc.webapp.model.NameFinder;
import uk.ac.ed.epcc.webapp.model.data.Composite;

/** Interface for a {@link Composite} that generates
 * a more user friendly name for an {@link AppUser}
 * but not a {@link NameFinder}
 * 
 * @author spb
 *
 */
public interface NameComposite<AU extends AppUser> {
	/** Get the presentation name
	 * 
	 * 
	 * @param target
	 * @return name or null
	 */
	public String getPresentationName(AU target);
}
