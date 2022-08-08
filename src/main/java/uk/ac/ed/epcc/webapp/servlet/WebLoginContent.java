package uk.ac.ed.epcc.webapp.servlet;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.config.FilteredProperties;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.content.ExtendedXMLBuilder;
import uk.ac.ed.epcc.webapp.content.UIGenerator;
import uk.ac.ed.epcc.webapp.forms.html.RedirectResult;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.MessageResult;
import uk.ac.ed.epcc.webapp.servlet.navigation.AbstractNodeMaker;
import uk.ac.ed.epcc.webapp.servlet.navigation.NavigationMenuService;
import uk.ac.ed.epcc.webapp.servlet.navigation.Node;
import uk.ac.ed.epcc.webapp.servlet.navigation.NodeMaker;
import uk.ac.ed.epcc.webapp.servlet.navigation.ParentNode;
import uk.ac.ed.epcc.webapp.session.AppUserFactory;
import uk.ac.ed.epcc.webapp.session.LoginRedirects;
import uk.ac.ed.epcc.webapp.session.SessionService;
/** Integrates external auth login links with the login page and navigation menus
 * 
 * This implements 
 * <ul>
 * <li> {@link UIGenerator} to add login links to the login page</li>
 * <li> {@link LoginRedirects} to handle the login links</li>
 * <li> {@link NodeMaker} to add identity binding links to the navigation menu</li>
 * </ul>
 * @author Stephen Booth
 *
 */
public class WebLoginContent extends AbstractNodeMaker implements UIGenerator, LoginRedirects {

	private final String tag;
	public WebLoginContent(AppContext conn, String tag) {
		super(conn);
		this.tag=tag;
	}

	
	/** This is the content added directly to the login page
	 * 
	 */
	@Override
	public ContentBuilder addContent(ContentBuilder builder) {
		AppContext conn = getContext();
		ServletService ss = getContext().getService(ServletService.class);
		//Give urls for alternate external auth login. Normally just the one but
		//can use comma seperated list to support multiple types.
		String login_urls = conn.getInitParameter("service.web_login.url");
		if(  login_urls != null ){ 
		String urls[] = login_urls.trim().split("\\s*,\\s*");
		String labels[] = conn.getInitParameter("service.web_login.login-text","Alternate login").split("\\s*,\\s*");
		String help[]= conn.getInitParameter("service.web_login.help-text","").split("\\s*,\\s*");
		for( int i = 0 ; i < urls.length ; i++){
			 ExtendedXMLBuilder a = builder.getSpan();
			 a.open("form");
			 a.addClass("button");
			 a.attr("method","post");
			 a.attr("action",ss.encodeURL("/LoginServlet"));
			 a.open("input");
			 a.attr("type","hidden");
			 a.attr("name","authtype");
			 a.attr("value",tag+":"+Integer.toString(i));
			 a.close();
			
			 a.open("input");
			 a.addClass("input_button");
			 a.addClass("login");
			 a.attr("type","submit");
			 String tt = help[i%help.length];
			 if( tt != null && ! tt.isEmpty()){
				a.attr("title",tt);
			 }
			 a.attr("value",labels[i%labels.length]);
			 a.close();
			 a.close(); // form
			 a.appendParent();
		 }
		 builder.getSpan().clean(" or ").appendParent();
		} 
		return builder;
	}
	/** Add registration links to the navigation menu
	 * 
	 */
	@Override
	public Node makeNode(String name, FilteredProperties props) {
		AppContext conn = getContext();
		AppUserFactory login_factory = conn.getService(SessionService.class).getLoginFactory();
		String login_urls = conn.getInitParameter("service.web_login.url");
		if(  login_urls != null && ! login_urls.isEmpty()){ 
			String urls[] = login_urls.trim().split("\\s*,\\s*");
			String labels[] = conn.getInitParameter(RemoteAuthServlet.SERVICE_WEB_LOGIN_UPDATE_TEXT,RemoteAuthServlet.REGISTER_IDENTITY_DEFAULT_TEXT).split("\\s*,\\s*");
			String help[] = conn.getInitParameter("service.web_login.help-text","").split("\\s*,\\s*");
			ParentNode parent = new ParentNode() ;
			parent.setMenuText("Register identities");
			parent.setHelpText("Before you can use your external credentials you have to register them here.");
			if( urls.length == 1 && ! conn.getService(NavigationMenuService.class).willAddChildren(name, props)){
				parent.setMenuText(labels[0]);
				parent.setTargetPath(urls[0]);
				if( help != null && help.length > 0 && help[0] != null && ! help[0].isEmpty()) {
					parent.setHelpText(help[0]);
				}
			}else{
				for( int i = 0 ; i < urls.length ; i++){
					ParentNode n = new ParentNode();
					n.setMenuText(labels[i]);
					n.setTargetPath(urls[i]);
					if( help != null && help.length > i ){
						String h = help[i];
						if( ! h.isEmpty()){
							n.setHelpText(h);
						}
					}
					parent.addChild(n);
				}
			}
			return parent;

		}
		
		return null;
	}


	@Override
	public FormResult getRedirect(String id) {
		try {
			AppContext conn = getContext();
			Integer i = Integer.parseInt(id);
			String login_urls = conn.getInitParameter("service.web_login.url");
			if(  login_urls != null ){ 
				String urls[] = login_urls.trim().split("\\s*,\\s*");
				if( i < 0 || i > urls.length) {
					return new MessageResult("invalid_argument");
				}
				// ok to make a session at this point
				return new RedirectResult(urls[i]);
			}
		}catch(Exception e) {
			getLogger().error("Error parsing alternate login", e);
		}
		return new MessageResult("invalid_argument");
	}

}
