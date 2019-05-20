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
import uk.ac.ed.epcc.webapp.servlet.TransitionServlet;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.AppUserFactory;
import uk.ac.ed.epcc.webapp.session.AppUserKey;
import uk.ac.ed.epcc.webapp.session.AppUserTransitionProvider;
import uk.ac.ed.epcc.webapp.session.CurrentUserKey;
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
		SessionService sess = getContext().getService(SessionService.class);
		AppUserTransitionProvider tp = AppUserTransitionProvider.getInstance(getContext());
		n.setTargetPath(TransitionServlet.getURL(getContext(), tp, sess.getCurrentPerson()));
		addChildrenFromTransition(n,props);
		String additions = props.getProperty(name+".head");
		if( additions != null && ! additions.trim().isEmpty()){
			addConfigNodes(n, props, additions.split(","));
		}
		return n;
	}
	
	private <AU extends AppUser> void addChildrenFromTransition(Node parent,FilteredProperties props){
		SessionService<AU> service = getContext().getService(SessionService.class);
		AppUserFactory<AU> fac = service.getLoginFactory();
		AU user = service.getCurrentPerson();
		
		// Add in the current-user transitions directly to the menu
		AppUserTransitionProvider<AU> prov = AppUserTransitionProvider.getInstance(getContext());
		for(AppUserKey<AU> key : prov.getTransitions(user)) {
			if( key instanceof CurrentUserKey && ((CurrentUserKey)key).addMenu(user)) {
				if( prov.allowTransition(getContext(), user, key)) {
					Node n = new ParentNode();
					n.setMenuText(prov.getText(key));
					n.setHelpText(prov.getHelp(key));
					n.setTargetPath(TransitionServlet.getURL(getContext(), prov,user,key));
					parent.addChild(n);
				}
			}
		}
	}



	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.servlet.navigation.AbstractNodeMaker#addChildren(uk.ac.ed.epcc.webapp.servlet.navigation.Node, java.lang.String, uk.ac.ed.epcc.webapp.config.FilteredProperties)
	 */
	@Override
	public void addChildren(Node parent, String name, FilteredProperties props) {
		String additions = props.getProperty(name+".tail", "Preferences,Role");
		if( additions != null && ! additions.trim().isEmpty()){
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