package uk.ac.ed.epcc.webapp.servlet.navigation;

import uk.ac.ed.epcc.webapp.model.data.Composite;
import uk.ac.ed.epcc.webapp.session.AppUserComposite;

/** Interface for {@link Composite}s that add nodes to a menu.
 * Usually {@link AppUserComposite}s
 * 
 * 
 * @see SUNodeMaker
 * @author Stephen Booth
 *
 */
public interface MenuContributor {

	public void addChildren(Node n);
}
