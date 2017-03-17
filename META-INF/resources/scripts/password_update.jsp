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
<%@ include file="/basic_session.jsf" %>
<%	
	String page_title = "Change "+service_name+" "+website_name+" Password";
	SessionService sess = conn.getService(SessionService.class);
	PasswordAuthComposite comp = (PasswordAuthComposite) sess.getLoginFactory().getComposite(PasswordAuthComposite.class);
	if( comp !=null && comp.mustResetPassword(sess.getCurrentPerson()) ){	
		// Need to complete this page
		request.setAttribute(NavigationMenuService.DISABLE_NAVIGATION_ATTR, Boolean.TRUE);
	}
%>
<%@ include file="/std_header.jsf" %>
<br/>
<%@ include file="/main__logged_in.jsf"%>
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
if( comp.mustResetPassword(sess.getCurrentPerson()) ){
%>
<div class="block">
<h2>Please change your <%=website_name %> password</h2>
<p class="warn">
Your <%=website_name %> password has expired and should be changed.
</p>
</div>
<%
}else{
%>
<%@ include file="/back.jsf" %>
<%} %>
<br/>
<%@ include file="/scripts/form_context.jsf" %>

<%
String default_charset = conn.getService(ServletService.class).defaultCharset();
HTMLForm f = new HTMLForm(conn);
PasswordUpdateFormBuilder fac = new PasswordUpdateFormBuilder(comp, true);
fac.buildForm(f,sess.getCurrentPerson(),conn);


%>
<div class="block">
<h2><%=page_title %></h2>
<p>
<%=fac.getPasswordPolicy() %>
</p>
 <form method="post" action="<%= response.encodeURL(web_path+"/UserServlet") %>"
<% if( default_charset != null && ! default_charset.isEmpty()){%> 
accept-charset="<%=default_charset %>"
<% } %>
 >
	<input type="hidden" name="action" value="CHANGE_PASSWORD"/>
	<input type='hidden' name='form_url' value='/scripts/password_update.jsp'/>
    <%= f.getHtmlFieldTable(request) %>
    <%= f.getActionButtons() %>
	</div>
</form>
</div>

<%
}
%>
<%@ include file="/std_footer.jsf" %>