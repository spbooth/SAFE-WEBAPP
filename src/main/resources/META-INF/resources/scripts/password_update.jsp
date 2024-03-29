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
   import="uk.ac.ed.epcc.webapp.*, uk.ac.ed.epcc.webapp.model.*, uk.ac.ed.epcc.webapp.session.*"
%>
<%--Must be only basic_session as this may be a required page --%>
<%@ taglib uri="http://safe.epcc.ed.ac.uk/webapp" prefix="wb" %>
<wb:ServiceInit/>
<wb:basic_session/>
<wb:formpage/>
<%	
	String page_title = "Change "+service_name+" "+website_name+" Password";
	SessionService sess = conn.getService(SessionService.class);
	PasswordAuthComposite comp = (PasswordAuthComposite) sess.getLoginFactory().getComposite(PasswordAuthComposite.class);
	if( comp !=null && comp.mustResetPassword(sess.getCurrentPerson()) ){	
		// Need to complete this page
		request.setAttribute(NavigationMenuService.DISABLE_NAVIGATION_ATTR, Boolean.TRUE);
	}
%>
<%@ include file="../std_header.jsf" %>
<br/>
<%@ include file="../main__logged_in.jsf"%>
<br/>
<%
if( comp == null || ! comp.canResetPassword(sess.getCurrentPerson()) ){
%>
<div class="block">
<p class="warn">
you cannot change the password for this account.<br/><br/>
	  Either this operation is not supported or the account is
	  in a state that does not permit password changes.
</p>
</div>
<%
}else{
	boolean must_change=comp.mustResetPassword(sess.getCurrentPerson());
if( must_change ){
%>
<div class="block">
<h1>Please change your <%=website_name %> password</h1>
<p class="warn">
Your <%=website_name %> password has expired and should be changed.
</p>
</div>
<%
}else{
%>
<%@ include file="../back.jsf" %>
<%} %>
<br/>
<wb:FormContext/>
<%@page import="uk.ac.ed.epcc.webapp.servlet.ServletService" %>
<%@page import="uk.ac.ed.epcc.webapp.forms.html.*" %>
<%
String default_charset = conn.getService(ServletService.class).defaultCharset();
HTMLForm f = new HTMLForm(conn);
// Don't require old password for a forced change
PasswordUpdateFormBuilder fac = new PasswordUpdateFormBuilder(comp, ! must_change);
fac.buildForm(f,sess.getCurrentPerson(),conn);


%>
<div class="block">
<h1><%=page_title %></h1>
<p>
<%=fac.getPasswordPolicy() %>
</p>
 <form method="post" action="<%= response.encodeURL(web_path+"/UserServlet") %>"
<% if( default_charset != null && ! default_charset.isEmpty()){%> 
accept-charset="<%=default_charset %>"
<% } %>
 >
	<input type="hidden" name="action" value="CHANGE_PASSWORD"/>
    <%= f.getHtmlFieldTable(request) %>
    <%= f.getActionButtons() %>
	</div>
</form>
</div>

<%
}
%>
<%@ include file="../std_footer.jsf" %>