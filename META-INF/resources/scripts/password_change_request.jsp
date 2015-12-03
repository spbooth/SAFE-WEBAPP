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
    password_update.jsp - Page used to change password.


   
--%>
<%@page import="uk.ac.ed.epcc.webapp.servlet.navigation.NavigationMenuService"%>
<%@ page	
   import="uk.ac.ed.epcc.webapp.*, uk.ac.ed.epcc.webapp.model.*, uk.ac.ed.epcc.webapp.session.*,uk.ac.ed.epcc.webapp.forms.html.*"
%>
<%@ include file="/service_init.jsf" %>
<%	
	String page_title = service_name+" Change Password";
	PageHTMLForm form = (PageHTMLForm) request.getAttribute("Form");
%>
<%if( form == null ){ %>
<jsp:forward page="/messages.jsp?message_type=access_denied" />
<%} %>
<%@ include file="/std_header.jsf" %>

<%@ include file="/scripts/form_context.jsf" %>
<div class="block">
<h2>Please set a password</h2>
<% if( form.hasSubmitted() && form.hasError()){ %>
<h3>This form contains errors:</h3>
<p class="warn">	
<%= form.getGeneralError() %>
</p>
<%} %>
<form>
<%= form.getHtmlForm() %>
<div class="action_buttons">
<input type="submit" value="Set Password"/>
</div>
</form>
</div>


<%@ include file="/std_footer.jsf" %>