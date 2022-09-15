package uk.ac.ed.epcc.webapp.session;

import uk.ac.ed.epcc.webapp.content.ContentBuilder;

/** Interface to allow classes that implement explicitly added
 * permissions for an {@link AppUser} to add content to a
 * permissions listing for a user.
 * 
 * @author Stephen Booth
 *
 * @param <AU>
 */
public interface PermissionSummary<AU extends AppUser> {
/** Add content showing explicitly added permission for an {@link AppUser}
 * 
 * @param user
 */
  public void addPermissionSummary(ContentBuilder cb,AU user);
}
