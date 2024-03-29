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
<%@page import="uk.ac.ed.epcc.webapp.servlet.navigation.NavigationMenuService"%>
<%@ page	
   import="uk.ac.ed.epcc.webapp.*, uk.ac.ed.epcc.webapp.model.*, uk.ac.ed.epcc.webapp.session.*,uk.ac.ed.epcc.webapp.forms.html.*"
%>
<%@page import="uk.ac.ed.epcc.webapp.forms.html.PageHTMLForm" %>
<%@page import="uk.ac.ed.epcc.webapp.content.HtmlBuilder" %>
<%@ taglib uri="http://safe.epcc.ed.ac.uk/webapp" prefix="wb" %>
<wb:ServiceInit/>
<wb:formpage/>
<%	
	String page_title = service_name+" Change "+website_name+" Password";
	PageHTMLForm form = (PageHTMLForm) request.getAttribute("Form");
	String policy = (String) request.getAttribute("policy");
	AppUser user = (AppUser) request.getAttribute("User");
	HtmlBuilder hb = new HtmlBuilder();
	if( user != null){
	hb.clean("You are setting a password for your account: ");
	hb.addObject(user.getFactory().getNames(user));
	}
%>
<%if( form == null ){ %>
<jsp:forward page="/messages.jsp?message_type=access_denied" />
<%} %>
<%@ include file="../std_header.jsf" %>

<div class="block" role="main">
<h1>Please set a password for use with the <%=website_name %></h1>
<p><%=hb.toString() %></p>
<p><%=policy %>
</p>
<%=form.getFormContext() %>
<form method="POST">
<%= form.getHtmlForm() %>
</form>
</div>


<%@ include file="../std_footer.jsf" %>