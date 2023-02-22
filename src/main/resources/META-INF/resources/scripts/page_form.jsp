<%--| Copyright - The University of Edinburgh 2021                             |--%>
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
    page_form.jsp

A simple page to display a PageForm from a Servlet. This is always intended to be accessed as a
forward from the servlet so all arguments are passed through the request.
   
--%>
<%@ page import="uk.ac.ed.epcc.webapp.forms.html.*" %>
<%@ taglib uri="http://safe.epcc.ed.ac.uk/webapp" prefix="wb" %>
<wb:formpage/>
<wb:css url="service_desk.css"/>
<%
PageHTMLForm form = (PageHTMLForm) request.getAttribute("Form");
String title = (String) request.getAttribute("Title");
Object extra = request.getAttribute("ExtraContent");
%>
<% if( form == null){ %>
<jsp:forward page="/messages.jsp?message_type=access_denied" />
<%} %>
<%@ page contentType="text/html;charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ page import="uk.ac.ed.epcc.webapp.content.HtmlBuilder" %>
<%@ taglib uri="http://safe.epcc.ed.ac.uk/webapp" prefix="wb" %>
<html lang="en">
<head>
 <title><%=title %></title>
 <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<wb:headings />
</head>
<body>
<header>
<wb:PageHeader/>
</header>
<div class="block">
<h2><%=title %></h2>
<% if( extra != null){
	HtmlBuilder hb = new HtmlBuilder();
	hb.addObject(extra);
%><p><%=hb.toString() %></p><%
}
%>
<div id="form">
<%=form.getFormContext() %>
<form method="POST">
<%= form.getHtmlForm() %>
</form>
</div>
</div>
<footer>
<wb:PageFooter/>
</footer>
</body>
</html>