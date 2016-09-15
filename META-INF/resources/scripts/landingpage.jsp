<%--| Copyright - The University of Edinburgh 2015                             |--%>
<%--|                                                                          |--%>
<%--| Licensed under the Apache License, Version 2.0 (the "License");          |--%>
<%--| you may not use this file except in compliance with the License.         |--%>
<%--| You may obtain a copy of the License at                                  |--%>
<%--|                                                                          |--%>
<%--|    http://www.apache.org/licenses/LICENSE-2.0                            |--%>
<%--|                                                                          |--%>
<%--| Unless required by applicable law or agreed to in writing, software      |--%>
<%--| distributed under the License is distributed on an "AS IS" BASIS,        |--%>
<%--| WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. |--%>
<%--| See the License for the specific language governing permissions and      |--%>
<%--| limitations under the License.                                           |--%>
<%--
 A generic - landing page that generates an in-page view of
 a fragment of the navigation menus.

--%>
<%@ include file="/basic_session.jsf" %>
<%@page import="uk.ac.ed.epcc.webapp.servlet.navigation.*" %>
<%
	String node_name = (String) request.getAttribute("MenuNode");
	if( node_name == null){
		node_name = request.getParameter("MenuNode");
	}
    
    String 	page_title=service_name+" "+website_name+ " "+node_name+" Menu";
%>
<%@ include file="/std_header.jsf"%>

<% if( NavigationMenuService.NAVIGATION_MENU_FEATURE.isEnabled(conn)){
	NavigationMenuService serv = conn.getService(NavigationMenuService.class);
	NodeContainer menu = serv.getMenu();
	HtmlBuilder builder = new HtmlBuilder();
	LandingPageVisitor vis = new LandingPageVisitor(conn,builder,node_name);
	menu.accept(vis);
%>
<%=builder.toString() %>
<%
}
%>

<%@ include file="/std_footer.jsf"%>