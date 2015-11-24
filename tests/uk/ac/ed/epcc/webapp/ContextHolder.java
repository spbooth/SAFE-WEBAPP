// Copyright - The University of Edinburgh 2014
package uk.ac.ed.epcc.webapp;

/** Interface for a class that can cache an AppContext provided after construction.
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.2 $")
public interface ContextHolder {
   public AppContext getContext();
   public void setContext(AppContext c);
}
