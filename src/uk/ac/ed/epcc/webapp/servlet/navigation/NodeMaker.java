// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.servlet.navigation;

import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.config.FilteredProperties;

/** A factory class for making {@link Node}s
 * 
 * Generic {@link NodeMaker}s just create the correct type of Node.
 * However these can also be plug-ins that build user specific node trees based on the current user and their database state.
 * 
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.2 $")
public interface NodeMaker extends Contexed{
	/** Create a {@link Node} for a named menu item.
	 * 
	 * @param name  name of the node 
	 * @param props {@link FilteredProperties} containing the navigation menu configuration
	 * @return {@link Node}
	 */
	public Node makeNode(String name, FilteredProperties props);
	
	/** Add additional child nodes specific to the {@link NodeMaker}.
	 * 
	 * This is intended for {@link Node}s based on the current user or their roles.
	 * It is a separate method so the dynamically generated nodes are added after any 
	 * specified in the configuration.
	 * 
	 * @param parent
	 * @param name
	 * @param props
	 */
	public void addChildren(Node parent, String name, FilteredProperties props);
}
