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
import uk.ac.ed.epcc.webapp.config.FilteredProperties;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.AppUserFactory;
import uk.ac.ed.epcc.webapp.session.MenuContributor;
import uk.ac.ed.epcc.webapp.session.SessionService;

/**
 * @author spb
 *
 */

public class SUNodeMaker extends AbstractNodeMaker  {

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
		addChildrenFromComposite(n,props);
		String additions = props.getProperty(name+".head", "Details");
		if( additions != null){
			addConfigNodes(n, props, additions.split(","));
		}
		return n;
	}
	
	private <AU extends AppUser> void addChildrenFromComposite(Node parent,FilteredProperties props){
		SessionService<AU> service = getContext().getService(SessionService.class);
		AppUserFactory<AU> fac = service.getLoginFactory();
		AU user = service.getCurrentPerson();
		
		for(MenuContributor<AU> mc : fac.getComposites(MenuContributor.class)){
			addConfigNodes(parent, props, mc.additionalMenuItems(user));
		}
	}



	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.servlet.navigation.AbstractNodeMaker#addChildren(uk.ac.ed.epcc.webapp.servlet.navigation.Node, java.lang.String, uk.ac.ed.epcc.webapp.config.FilteredProperties)
	 */
	@Override
	public void addChildren(Node parent, String name, FilteredProperties props) {
		String additions = props.getProperty(name+".tail", "Preferences,Role");
		if( additions != null){
			addConfigNodes(parent, props, additions.split(","));
		}
	}



	/**
	 * @param parent
	 * @param props
	 * @param role
	 */
	public void addConfigNodes(Node parent, FilteredProperties props, String names[]) {
		if(names==null || names.length == 0){
			return;
		}
		NavigationMenuService serv = getContext().getService(NavigationMenuService.class);
				for(String role: names){
			Node r = serv.makeNode(new HashSet<String>(), role, props);
			if( r != null){
				parent.addChild(r);
			}
		}
	}

}