// Copyright - The University of Edinburgh 2015
/** Support for a menu navigation.
 * <p>
 * A navigation menu consists of a tree of Navigational nodes that will be formatted as a html multi-level list of links.
 * CSS and possible ECMAscript will format these as navigational menus. The exact details of which 
 * depend on the css and may be responsive to the device viewing the page. 
 * <p>
 * Each node can represent:
 * <ul>
 * <li>The short text presented to the user in the navigational menu.</li>
 * <li>A target url navigated to when the node is clicked.</li>
 * <li>A set of sub-nodes</i>
 * <li>An query method that checks if the current request "matches" the urls controlled by the node. This will be used to generate an attribute on the html node. This must include the target url but can be wider. Child targets do not have to be explicitly included the CSS can explicitly match active children.</li> 
 * </ul> 
 * Nodes are lightweight and serialisable so a menu can be built and cached in the user session without requiring complex sql queries.
 * The Node tree is build by a AppContextService.
 * Operations that might modify the menu structure should use the service to explicitly rebuild the tree. 
 * 
 * @author spb
 *
 */
package uk.ac.ed.epcc.webapp.servlet.navigation;