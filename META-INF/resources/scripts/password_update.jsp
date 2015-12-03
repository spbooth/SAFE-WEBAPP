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
	String page_title = service_name+" Change Password";
	SessionService sess = conn.getService(SessionService.class);
	PasswordAuthComposite comp = (PasswordAuthComposite) sess.getLoginFactory().getComposite(PasswordAuthComposite.class);
	if( comp.mustResetPassword(sess.getCurrentPerson()) ){	
		// Need to complete this page
		request.setAttribute(NavigationMenuService.DISABLE_NAVIGATION_ATTR, Boolean.TRUE);
	}
%>
<%@ include file="/std_header.jsf" %>
<br/>
<%@ include file="/main__logged_in.jsf"%>
<br/>
<%

if( comp.mustResetPassword(sess.getCurrentPerson()) ){
%>
<div class="block">
<h2>Please change your website password</h2>
<p class="warn">
Your website password has expired and should be changed
</p>
</div>

<%
}else{
%>
<%@ include file="/back.jsf" %>
<%} %>
<br/>
<% if("cannot_change".equals(request.getParameter("error"))) { %>
<div class="block">
<p class="warn">
you cannot change the password for this account.<br/><br/>
	  Either this operation is not supported or the account is
	  in a state that does not permit password changes.
</p>
</div>
<% } %>
<% if("bad_password".equals(request.getParameter("error"))) { %>
<div class="block">
<p class="warn">
Please enter your current valid password.
</p>
</div>
<% } %>
<% if("password_mismatch".equals(request.getParameter("error"))) { %>
<div class="block">
<p class="warn">
New passwords do not match.<br/><br/>
	  Please type your new password properly in both boxes.
</p>
</div>
<% } %>
<% if("short_password".equals(request.getParameter("error"))) { %>
<div class="block">
<p class="warn">
Your new password is too short.<br/><br/>
	  Please enter a new password longer than 6 characters.
	  </p>
	  </div>
<% } %>
<% if("unchanged_password".equals(request.getParameter("error"))) { %>
<div class="block">
<p class="warn">
Your new password is the same as the old one.<br/><br/>
	  Please enter a new password.
	  </p>
	  </div>
<% } %>
<div class="block">
<h2>Change Web Password</h2>
<p>This form allows you to change the password you use to log into this web-site.</p>
  <form method="post" action="<%= response.encodeURL(web_path+"/UserServlet") %>">
	<input type="hidden" name="action" value="CHANGE_PASSWORD"/>
    <table class="form">
	<tr>
	  <th>Current Password:</th>
	  <td>
		<input type="password" name="password" class="input" >
	  </td>
	</tr>

	<tr>
	
	  <th>New Password:</th>
	  <td>
		<input type="password" name="password1" class="input" >
	  </td>
	</tr>
	<tr>
	  
	  <th>New Password (again):</th>
	 <td>
		<input type="password" name="password2" class="input" >
	  </td>
	  
	</tr>
	</table>
	<div class="action_buttons">
	<input class="input_button" type="submit" value=" Change " name="submit"/>
	</div>
  </form>
</div>

<%@ include file="/std_footer.jsf" %>