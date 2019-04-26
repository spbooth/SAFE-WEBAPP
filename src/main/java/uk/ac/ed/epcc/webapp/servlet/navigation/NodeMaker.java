//| Copyright - The University of Edinburgh 2015                            |
//|                                                                         |
//| Licensed under the Apache License, Version 2.0 (the "License");         |
//| you may not use this file except in compliance with the License.        |
//| You may obtain a copy of the License at                                 |
//|                                                                         |
//|    http://www.apache.org/licenses/LICENSE-2.0                           |
//|                                                                         |
//| Unless required by applicable law or agreed to in writing, software     |
//| distributed under the License is distributed on an "AS IS" BASIS,       |
//| WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.|
//| See the License for the specific language governing permissions and     |
//| limitations under the License.                                          |
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

public interface NodeMaker extends Contexed{
	/** Create a {@link Node} for a named menu item.
	 * 
	 * This is allowed to return null to indicate that nodes of this type are disabled/empty and should be
	 * suppressed. The {@link NavigationMenuService} can be configured to substitute a different node
	 * in this case (for example to host child nodes added by the configuration). 
	 * <p>
	 * Automatic child nodes can be added in this method and will appear first. Followed by nodes from the configuration
	 * then nodes from {@link #addChildren(Node, String, FilteredProperties)}
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
	 * specified in the configuration. Dynamic nodes that should appear first can be
	 * added directly in {@link #makeNode(String, FilteredProperties)}
	 * 
	 * 
	 * @param parent
	 * @param name
	 * @param props
	 */
	public void addChildren(Node parent, String name, FilteredProperties props);
}