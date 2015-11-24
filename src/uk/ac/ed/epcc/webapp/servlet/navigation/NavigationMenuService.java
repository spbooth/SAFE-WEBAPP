// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.servlet.navigation;

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
import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory;
import uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactory;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
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
 * </ul>
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.10 $")
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
	public static final Feature NAVIGATION_MENU_FEATURE = new Feature("navigation_menu", false, "Support for navigation menu code");
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
		NodeContainer menu = (NodeContainer) service.getAttribute(NAVIGATION_MENU_ATTR);
		if( menu == null ){
			menu = makeMenu();
			if( menu != null ){
				service.setAttribute(NAVIGATION_MENU_ATTR, menu);
			}
		}
		return menu;
	}
	
	public void resetMenu(){
		SessionService service = getContext().getService(SessionService.class);
		if( service != null ){
			service.removeAttribute(NAVIGATION_MENU_ATTR);
		}
	}
	/** Populate a {@link NodeContainer} with the appropriate navigational {@link Node}s for the current user.
	 * @return {@link NodeContainer} for the navigation menu or null
	 */
	private NodeContainer makeMenu() {
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
		try{
			String path = conn.expandText(menu_prop.getProperty(name+".path"));
			String type = conn.expandText(menu_prop.getProperty(name+".type","default_node_type"));
			String child_list = conn.expandText(menu_prop.getProperty(name+".list"));
			String image = menu_prop.getProperty(name+".image");
			NodeMaker maker = getContext().makeObjectWithDefault(NodeMaker.class, ParentNodeMaker.class, type);
			Node n = maker.makeNode(name, menu_prop);
			if( n != null ){
				// Default to maker set text or failing that name
				String menu_text = n.getMenuText(getContext());
				n.setMenuText(conn.expandText(menu_prop.getProperty(name+".text", menu_text == null ? name : menu_text)));
				n.setImage(image);
				if( path != null){
					n.setTargetPath(path);
				}
				// Add config nodes first
				if( child_list != null ){
					for(String child_name : child_list.trim().split("\\s*,\\s*")){
						Node c = makeNode(seen,child_name, menu_prop);
						if( c != null ){
							n.addChild(c);
						}
					}
				}
				maker.addChildren(n, name, menu_prop);
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
			HtmlBuilder panel = (HtmlBuilder) builder.getPanel("nav");
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
