// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.session;

import uk.ac.ed.epcc.webapp.model.data.Composite;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;

/** An interface for {@link Composite}s that contribute to commits.
 * @author spb
 * @param <AU> 
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.1 $")
public interface AppUserCommitObserver<AU extends AppUser> {
  public void pre_commit(AU person,boolean dirty) throws DataFault;
  public void post_commit(AU person, boolean changed)throws DataFault;
}
