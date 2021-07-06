package uk.ac.ed.epcc.webapp.servlet;

import uk.ac.ed.epcc.webapp.AbstractContexed;
import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.content.ExtendedXMLBuilder;
import uk.ac.ed.epcc.webapp.content.UIGenerator;

public class WebLoginContent extends AbstractContexed implements UIGenerator {

	public WebLoginContent(AppContext conn) {
		super(conn);
	}

	@Override
	public ContentBuilder addContent(ContentBuilder builder) {
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
			 a.attr("value",Integer.toString(i));
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

}
