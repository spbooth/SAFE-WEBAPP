package uk.ac.ed.epcc.webapp.session;

/** A required page that can trigger side-effects if not completed in a timely manner
 * 
 * e.g. locking accounts etc.
 * 
 * @author Stephen Booth
 *
 * @param <U> AppUser type
 */
public interface RequiredPageWithAction<U extends AppUser> extends RequiredPage<U>, RequiredPageAction<U> {

	
}
