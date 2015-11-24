// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.session;

import java.util.Set;

/**
 * @author spb
 *
 * @param <AU>
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.2 $")
public interface RequiredPageProvider<AU extends AppUser> {

	/** Get the set of required pages.
	 * 
	 * @return Set of RequiredPage objects.
	 */
	public abstract Set<RequiredPage<AU>> getRequiredPages();

}