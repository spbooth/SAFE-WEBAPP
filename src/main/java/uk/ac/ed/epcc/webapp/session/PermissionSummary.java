package uk.ac.ed.epcc.webapp.session;

import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;

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
  
  /** hook to clear all permissions on the user
   * 
   * @param user
   * @throws DataFault
   */
  public void clearPermissions(AU user) throws DataFault;
}
