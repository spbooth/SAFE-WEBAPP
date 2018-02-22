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

import java.util.Date;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.AppContextService;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.PreRequisiteService;
import uk.ac.ed.epcc.webapp.config.ConfigService;
import uk.ac.ed.epcc.webapp.config.FilteredProperties;
import uk.ac.ed.epcc.webapp.content.HtmlBuilder;
import uk.ac.ed.epcc.webapp.content.HtmlBuilder.Panel;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory;
import uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactory;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.preferences.Preference;
import uk.ac.ed.epcc.webapp.servlet.TransitionServlet;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** A {@link AppContextService} for building navigation menus.
 * <p>
 * The top level menu is defined as a comma separated list in the property <b>navigation.<em>list</em></b>
 * Each name defines a menu node using the properties:
 * <ul>
 * <li> <b>navigation.<em>name</em>.text</b> - menu text, defaults to <em>name</em></li> 
 * <li> <b>navigation.<em>name</em>.list</b> - comma separated list of additional child node names</li> 
 * <li> <b>navigation.<em>name</em>.type</b> - type of {@link NodeMaker} to make node</li> 
 * <li> <b>navigation.<em>name</em>.path</b> - path to set for the node</li> 
 * <li> <b>navigation.<em>name</em>.help</b> - tooltip help text</li> 
 * <li> <b>navigation.<em>name</em>.path</b> - path to set for the node</li> 
 * <li> <b>navigation.<em>name</em>.role</b> - Role user must have to see item</li> 
 * <li> <b>navigation.<em>name</em>.require_feature</b> - Feature that must be enabled to see item</li> 
 * <li> <b>navigation.<em>name</em>.disable_feature</b> - Feature that must be disabled to see item</li> 
 * <li> <b>navigation.<em>name</em>.require_parameter</b> - config parameter that must be non null to see item</li> 
 * <li> <b>navigation.<em>name</em>.image</b> - image name for menu item</li>
 * <li> <b>navigation.<em>name</em>.access_key</b> - access_key the node</li> 
 * <li> <b>navigation.<em>name</em>.replacement</b> - Name of a node to substitute if the {@link NodeMaker} returns null. The configuration properties of the original node are not automatically inherited. 
 * </ul>
 * @author spb
 *
 */

@PreRequisiteService({SessionService.class,ConfigService.class})
public class NavigationMenuService extends Object implements Contexed, AppContextService<NavigationMenuService>{

	/**
	 * 
	 */
	public static final String DISABLE_NAVIGATION_ATTR = "DisableNavigation";
	/**
	 * 
	 */
	private static final String NAVIGATION_MENU_ATTR = "NavigationMenu";
	public static final String NAVIGATIONAL_PREFIX = "navigation";
	public static final Feature NAVIGATION_MENU_FEATURE = new Preference("navigation_menu", false, "Support for navigation menu code");
	public static final Feature NAVIGATION_MENU_JS_FEATURE = new Preference("navigation_menu.script", false, "Add javascript for keyboard access to navigaition menu sub-menus");

	private final AppContext conn;
	/**
	 * 
	 */
	public NavigationMenuService(AppContext conn) {
		this.conn=conn;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.AppContextService#cleanup()
	 */
	@Override
	public void cleanup() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.AppContextService#getType()
	 */
	@Override
	public Class<? super NavigationMenuService> getType() {
		return NavigationMenuService.class;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Contexed#getContext()
	 */
	@Override
	public AppContext getContext() {
		return conn;
	}
	
	public NodeContainer getMenu(){
		SessionService service = getContext().getService(SessionService.class);
		if( service == null || ! service.haveCurrentUser() || ! NAVIGATION_MENU_FEATURE.isEnabled(conn)){
			return null;
		}
		NodeContainer menu = null;
		try{
			menu = (NodeContainer) service.getAttribute(NAVIGATION_MENU_ATTR);
		}catch(Throwable t){
			// Any de-serialisation problem will probably jus destory the session
			// but just to be fail-safe trap all throwables.
		}
		Date too_old = new Date(System.currentTimeMillis()-getContext().getLongParameter("navigation.expire_millis", 600000));
		if( menu == null || menu.getDate().before(too_old)){
			menu = makeMenu();
			if( menu != null ){
				service.setAttribute(NAVIGATION_MENU_ATTR, menu);
			}
		}
		return menu;
	}
	/** reset the Navigation menu so it is re-calculated for the next page view.
	 * This needs to be called when the account state changes in a way that will
	 * change the menu structure.
	 * 
	 */
	public void resetMenu(){
		SessionService service = getContext().getService(SessionService.class);
		if( service != null ){
			service.removeAttribute(NAVIGATION_MENU_ATTR);
		}
	}
	/** Populate a {@link NodeContainer} with the appropriate navigational {@link Node}s for the current user.
	 * @return {@link NodeContainer} for the navigation menu or null
	 */
	public NodeContainer makeMenu() {
		ConfigService cfg = getContext().getService(ConfigService.class);
		if( cfg==null){
			return null;
		}
		SessionService<?> sess = getContext().getService(SessionService.class);
		if( sess == null || ! sess.haveCurrentUser()){
			return null;
		}
		Properties prop = cfg.getServiceProperties();
		FilteredProperties menu_prop = new FilteredProperties(prop, NAVIGATIONAL_PREFIX);
		String list = menu_prop.getProperty("list");
		if( list == null ){
			return null;
		}
		NodeContainer menu = new NodeContainer();
		Set<String> seen = new HashSet<String>();
		for(String name : list.trim().split("\\s*,\\s*")){
			Node n = makeNode(seen,name, menu_prop);
			if( n != null ){
				menu.addChild(n);
			}
		}
		return menu;
	}

	/**
	 * @param name
	 * @param menu_prop
	 * @return
	 */
	protected Node makeNode(Set<String> seen, String name, FilteredProperties menu_prop) {
		// Avoid circular loops
		if( seen.contains(name)){
			return null;
		}
		seen.add(name);
		// optional required role
		String role=conn.expandText(menu_prop.getProperty(name+".role"));
		if( role != null && ! conn.getService(SessionService.class).hasRoleFromList(role.split("\\s*,\\s*"))){
			return null;
		}
		String required_feature = menu_prop.getProperty(name+".required_feature");
		if( required_feature != null && ! Feature.checkDynamicFeature(conn, required_feature, false)){
			return null;
		}
		String disable_feature = menu_prop.getProperty(name+".disable_feature");
		if( disable_feature != null && Feature.checkDynamicFeature(conn, disable_feature, false)){
			return null;
		}
		String required_parameter = menu_prop.getProperty(name+".require_parameter");
		if( required_parameter != null && conn.getInitParameter(required_parameter, null) == null){
			return null;
		}
		try{
			String path = conn.expandText(menu_prop.getProperty(name+".path"));
			String help = conn.expandText(menu_prop.getProperty(name+".help"));
			String type = conn.expandText(menu_prop.getProperty(name+".type","default_node_type"));
			String child_list = conn.expandText(menu_prop.getProperty(name+".list"));
			String image = menu_prop.getProperty(name+".image");
			String key = menu_prop.getProperty(name+".accesskey");
			NodeMaker maker = getContext().makeObjectWithDefault(NodeMaker.class, ParentNodeMaker.class, type);
			Node n = maker.makeNode(name, menu_prop);
			if( n != null ){
				n.setID(name);
				// Default to maker set text or failing that name
				String menu_text = n.getMenuText(getContext());
				n.setMenuText(conn.expandText(menu_prop.getProperty(name+".text", menu_text == null ? name : menu_text)));
				n.setImage(image);
				if( path != null){
					n.setTargetPath(path);
				}
				if( key != null && key.length() > 0){
					n.setAccessKey(key.charAt(0));
				}
				if( help != null && ! help.isEmpty()){
					n.setHelpText(help);
				}
				// Add config nodes first
				if( child_list != null && ! child_list.isEmpty()){
					for(String child_name : child_list.trim().split("\\s*,\\s*")){
						Node c = makeNode(seen,child_name, menu_prop);
						if( c != null ){
							n.addChild(c);
						}
					}
				}
				maker.addChildren(n, name, menu_prop);
			}else{
				// A null node may just be disabled by the maker look for a substitute node
				String replacement = menu_prop.getProperty(name+".replacement");
				if( replacement != null ){
					return makeNode(seen,replacement,menu_prop);
				}
			}
			return n;
		}catch(Throwable t){
			conn.getService(LoggerService.class).getLogger(getClass()).error("Problem making menu "+name,t);
			return null;
		}
	}

	public void disableNavigation(HttpServletRequest request){
		request.setAttribute(DISABLE_NAVIGATION_ATTR, Boolean.TRUE);
	}
	/** build the navigation element for 
	 * 
	 * @param builder
	 * @return
	 */
	public HtmlBuilder getNavigation(HttpServletRequest request, HtmlBuilder builder){
		NodeContainer nav = getMenu();
		if( nav != null && ! nav.isEmpty() && request.getAttribute(DISABLE_NAVIGATION_ATTR)==null){
			HtmlBuilder.Panel panel =  (Panel) builder.getPanel("nav");
			panel.addAttr("role", "navigation");
			//panel.addHeading(2, "Navigation");
			NodeGenerator gen = new NodeGenerator(getContext(), nav, request);
			gen.addContent(panel);
			panel.addParent();
		}
		return builder;
	}
	/** Make a node corresponding to the view url of a target.
	 * 
	 * @param conn
	 * @param provider
	 * @param target
	 * @return {@link Node}
	 */
	public static <T,K> Node viewNode(AppContext conn,ViewTransitionFactory<K, T> provider, T target){
		Node n = new ParentNode();
		n.setTargetPath(TransitionServlet.getURL(conn, provider, target));
		return n;
	}
	/** Make a node corresponding to a transition a target.
	 * 
	 * @param conn
	 * @param provider
	 * @param target
	 * @return {@link Node}
	 */
	public static <T,K> Node transitionNode(AppContext conn,TransitionFactory<K, T> provider, T target, K key){
		Node n = new ExactNode();
		n.setTargetPath(TransitionServlet.getURL(conn, provider, target,key));
		return n;
	}
}