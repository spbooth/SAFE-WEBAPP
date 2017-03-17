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
package uk.ac.ed.epcc.webapp.session;

import java.util.Iterator;
import java.util.Set;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.config.FilteredProperties;
import uk.ac.ed.epcc.webapp.servlet.navigation.AbstractNodeMaker;
import uk.ac.ed.epcc.webapp.servlet.navigation.Node;
import uk.ac.ed.epcc.webapp.servlet.navigation.NodeMaker;
import uk.ac.ed.epcc.webapp.servlet.navigation.PageNode;
import uk.ac.ed.epcc.webapp.servlet.navigation.ParentNode;

/** A {@link NodeMaker} that adds role toggle links.
 * @author spb
 *
 */

public class RoleNodeMaker extends AbstractNodeMaker {

	/**
	 * @param conn
	 */
	public RoleNodeMaker(AppContext conn) {
		super(conn);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.servlet.navigation.NodeMaker#makeNode(java.lang.String, uk.ac.ed.epcc.webapp.config.FilteredProperties)
	 */
	@Override
	public Node makeNode(String name, FilteredProperties props) {
		SessionService<?> session_service = getContext().getService(SessionService.class);
		Set<String> toggleRoles = session_service.getToggleRoles();
		if( toggleRoles == null || toggleRoles.size() == 0){
			return null;
		}
		// path and text set by properties
		ParentNode node = new ParentNode();
		node.setMenuText("Toggle roles");
		return node;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.servlet.navigation.AbstractNodeMaker#addChildren(uk.ac.ed.epcc.webapp.servlet.navigation.Node, java.lang.String, uk.ac.ed.epcc.webapp.config.FilteredProperties)
	 */
	@Override
	public void addChildren(Node parent, String name, FilteredProperties props) {
		SessionService<?> session_service = getContext().getService(SessionService.class);
		 for(Iterator<String> it=session_service.getToggleRoles().iterator(); it.hasNext(); ){
			  String role= it.next();
			  String role_name=role;
			  PageNode node = new PageNode();
			  node.setTargetPath("/UserServlet/action=TOGGLE_PRIV/role="+role);
			  if( session_service.hasRole(role_name)){
				  node.setMenuText("-"+role_name);
				  node.setDisplayClass("has_role");
				  node.setHelpText("Remove access to role "+role_name);
			  }else{
				  node.setMenuText("+"+role_name);
				  node.setDisplayClass("missing_role");
				  node.setHelpText("Enable access to role "+role_name);
			  }
			  parent.addChild(node);
		 }
	}

}