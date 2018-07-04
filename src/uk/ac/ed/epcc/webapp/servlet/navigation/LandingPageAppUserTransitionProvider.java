//| Copyright - The University of Edinburgh 2018                            |
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
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.html.RedirectResult;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractDirectTransition;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.AppUserKey;
import uk.ac.ed.epcc.webapp.session.AppUserTransitionProvider;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** An {@link AppUserTransitionProvider} where the view transition can act
 * as the landing-page for the {@link SUNodeMaker} menu.
 * @author Stephen Booth
 *
 */
public class LandingPageAppUserTransitionProvider extends AppUserTransitionProvider {

	/** An {@link AppUserKey} for menu items  imported as transitions
	 * 
	 * @author Stephen Booth
	 *
	 */
	public static class ImportKey extends AppUserKey{

		/**
		 * @param name
		 * @param text
		 * @param help
		 */
		public ImportKey(String name, String text, String help) {
			super(name, text, help);
		}

		/**
		 * @param name
		 * @param help
		 */
		public ImportKey(String name, String help) {
			super(name, help);
		}

		/**
		 * @param name
		 */
		public ImportKey(String name) {
			super(name);
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.session.AppUserKey#allow(uk.ac.ed.epcc.webapp.session.AppUser, uk.ac.ed.epcc.webapp.session.SessionService)
		 */
		@Override
		public boolean allow(AppUser user, SessionService op) {
			return op.isCurrentPerson(user);
		}
		
	}
	
	public static class MenuTransition extends AbstractDirectTransition<AppUser>{
		/**
		 * @param url
		 */
		public MenuTransition(String url) {
			super();
			this.url = url;
		}

		private final String url;

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.transition.DirectTransition#doTransition(java.lang.Object, uk.ac.ed.epcc.webapp.AppContext)
		 */
		@Override
		public FormResult doTransition(AppUser target, AppContext c) throws TransitionException {
			return new RedirectResult(url);
		}
	}
	
	public class AddTransitionvisitor implements Visitor{
		public boolean recurse=false;
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.servlet.navigation.Visitor#visitContainer(uk.ac.ed.epcc.webapp.servlet.navigation.NodeContainer)
		 */
		@Override
		public void visitContainer(NodeContainer container) {
			for(Node n : container.getChildren()) {
				visitNode(n);
			}
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.servlet.navigation.Visitor#visitNode(uk.ac.ed.epcc.webapp.servlet.navigation.Node)
		 */
		@Override
		public void visitNode(Node n) {
			String targetPath = n.getTargetPath(getContext());
			if( n.isEmpty() && targetPath != null && ! n.useLandingPage(getContext())) {
				try {
				addTransition(new ImportKey(n.getID(), n.getMenuText(getContext()),n.getHelpText()), new MenuTransition(targetPath));
				}catch(Throwable t) {
					getLogger().error("Error adding transition "+n.getID(), t);
				}
			}
			if( recurse) {
				visitContainer(n);
			}
		}
		
	}
	/**
	 * @param c
	 */
	public LandingPageAppUserTransitionProvider(AppContext c) {
		super(c);
		// Add the top-level leaf menu items as transitions
		SUNodeMaker maker = new SUNodeMaker(getContext());
		Node top = new ParentNode();
		top.setID("Person");
		NavigationMenuService nav = getContext().getService(NavigationMenuService.class);
		nav.addChildren(new HashSet<String>(), "Person", nav.getProperties(), maker, top);
		
		AddTransitionvisitor vis = new AddTransitionvisitor();
		vis.recurse=false;
		vis.visitContainer(top);
	}
	

	@Override
	public <X extends ContentBuilder> X getTopContent(X cb, AppUser target, SessionService<?> sess) {
		SUNodeMaker maker = new SUNodeMaker(getContext());
		Node top = new ParentNode();
		top.setID("Person");
		NavigationMenuService nav = getContext().getService(NavigationMenuService.class);
		nav.addChildren(new HashSet<String>(), "Person", nav.getProperties(), maker, top);
	
		// Add sub-menus
		for(Node n : top.getChildren()) {
			if( ! n.isEmpty()) {
				String targetPath = n.getTargetPath(getContext());
				if( targetPath != null) {
					ContentBuilder item = cb.getPanel("bar");
					ContentBuilder header = item.getHeading(2);
					header.addText(n.getHelpText());
					header.addButton(getContext(), n.getMenuText(getContext()),new RedirectResult(targetPath));
					header.addParent();
					item.addParent();
				}
			}
		}
		return cb;
	}


}
