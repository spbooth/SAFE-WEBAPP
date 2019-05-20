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
<%@ include file="/service_init.jsf" %>
<wb:formpage/>
<%	
	String page_title = service_name+" Change "+website_name+" Password";
	PageHTMLForm form = (PageHTMLForm) request.getAttribute("Form");
	String policy = (String) request.getAttribute("policy");
%>
<%if( form == null ){ %>
<jsp:forward page="/messages.jsp?message_type=access_denied" />
<%} %>
<%@ include file="/std_header.jsf" %>

<%@ include file="/scripts/form_context.jsf" %>
<div class="block" role="main">
<h2>Please set a password for use with the <%=website_name %></h2>
<% if( form.hasSubmitted() && form.hasError()){ %>
<h3>This form contains errors:</h3>
<p class="warn">	
<% String error = form.getGeneralError(); %>
<%= error == null ? "" : error %>
</p>
<%} %>
<p><%=policy %>
</p>
<form>
<%= form.getHtmlForm() %>
</form>
</div>


<%@ include file="/std_footer.jsf" %>