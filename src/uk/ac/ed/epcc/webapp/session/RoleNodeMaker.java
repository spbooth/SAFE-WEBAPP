// Copyright - The University of Edinburgh 2015
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
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.3 $")
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
			  }else{
				  node.setMenuText("+"+role_name);
				  node.setDisplayClass("missing_role");
			  }
			  parent.addChild(node);
		 }
	}

}
