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

import java.util.HashSet;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.config.FilteredProperties;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.AppUserFactory;
import uk.ac.ed.epcc.webapp.session.MenuContributor;
import uk.ac.ed.epcc.webapp.session.RoleNodeMaker;
import uk.ac.ed.epcc.webapp.session.SessionService;

/**
 * @author spb
 *
 */

public class SUNodeMaker extends AbstractNodeMaker  {

	public static final Feature UPDATE_DETAILS_FEATURE = new Feature("update_person_details", true, "The auto generated person menu contains a link to update personal details");
	public static final Feature PREFERENCE_MENU_FEATURE = new Feature("person_menu.preferences", true, "The auto generated person menu contains a link to preference transitions");
	public static final Feature TOGGLE_ROLE_MENU_FEATURE = new Feature("person_menu.toggle_roles", true, "The auto generated person menu contains role toggle buttons");
	/**
	 * @param conn 
	 * 
	 */
	public SUNodeMaker(AppContext conn) {
		super(conn);
	}

	

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.servlet.navigation.NodeMaker#makeNode(java.lang.String, uk.ac.ed.epcc.webapp.config.FilteredProperties)
	 */
	@Override
	public Node makeNode(String name, FilteredProperties props) {
		SUNode n = new SUNode();
		addChildrenFromComposite(n);
		if( UPDATE_DETAILS_FEATURE.isEnabled(getContext())){
			Node u = new ParentNode();
			u.setMenuText("Update personal details");
			u.setHelpText("Update the information we hold about you");
			u.setTargetPath("/scripts/personal_update.jsp");
			n.addChild(u);
		}
		return n;
	}
	
	private <AU extends AppUser> void addChildrenFromComposite(Node parent){
		SessionService<AU> service = getContext().getService(SessionService.class);
		AppUserFactory<AU> fac = service.getLoginFactory();
		AU user = service.getCurrentPerson();
		
		for(MenuContributor<AU> mc : fac.getComposites(MenuContributor.class)){
			mc.addMenuItems(parent, user);
		}
	}



	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.servlet.navigation.AbstractNodeMaker#addChildren(uk.ac.ed.epcc.webapp.servlet.navigation.Node, java.lang.String, uk.ac.ed.epcc.webapp.config.FilteredProperties)
	 */
	@Override
	public void addChildren(Node parent, String name, FilteredProperties props) {
		if( PREFERENCE_MENU_FEATURE.isEnabled(getContext())){
			Node p = new ParentNode();
			p.setMenuText("Preferences");
			p.setTargetPath("/TransitionServlet/Preferences");
			parent.addChild(p);
		}
		if(TOGGLE_ROLE_MENU_FEATURE.isEnabled(getContext())){
			// Use menu props
			NavigationMenuService serv = getContext().getService(NavigationMenuService.class);
			Node r = serv.makeNode(new HashSet<String>(), "Role", props);
			if( r != null){
				parent.addChild(r);
			}
		}
		
	}

}