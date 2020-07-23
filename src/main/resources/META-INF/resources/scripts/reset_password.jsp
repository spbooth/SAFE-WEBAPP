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
<%@ page session="false"
	import="uk.ac.ed.epcc.webapp.*, uk.ac.ed.epcc.webapp.model.*,uk.ac.ed.epcc.webapp.forms.html.*,java.util.*"%>
<%@page import="uk.ac.ed.epcc.webapp.logging.*" %>
<%@page import="uk.ac.ed.epcc.webapp.session.*" %>
<%@ include file="/service_init.jsf"%>
<%
    if( conn == null ){
%>
<jsp:forward page="/messages.jsp">
<jsp:param name="message_type" value="internal_error"/>
<jsp:param name="message_extra" value="No AppContext"/>
</jsp:forward>
<%
	  return;
    }
    Logger log =conn.getService(LoggerService.class).getLogger(AppContext.class);
    log.debug("In reset_password.jsp");
%>
<%
    SessionService<?> serv = conn.getService(SessionService.class);
	if( serv == null ){
%>
<jsp:forward page="/messages.jsp">
<jsp:param name="message_type" value="internal_error"/>
<jsp:param name="message_extra" value="Logins are not configured for this service"/>
</jsp:forward>
<%
	   return;
	}
	AppUserFactory<?> fac = serv.getLoginFactory();
	log.debug("Login serv is "+serv);
	if(fac == null ||  ! fac.isValid()){
		%>
<jsp:forward page="/messages.jsp">
<jsp:param name="message_type" value="internal_error"/>
<jsp:param name="message_extra" value="No Login Factory"/>
</jsp:forward>
		<%
		return;
	}
	if(serv != null && serv.haveCurrentUser()) {
		 log.debug("We have a user");
%>
<jsp:forward page="/scripts/password_update.jsp" />
<%	
		return;
	}
	log.debug("Showing reset password page");
	String page_title = service_name+" "+website_name+" Password recovery";
%>
<wb:formpage/>
<%@ include file="/std_header.jsf"%>
<%
    PasswordAuthComposite password_auth = fac.getComposite(PasswordAuthComposite.class);
	if( password_auth == null ){
%>
<div class="block">
<H1>Password authentication disabled</H1>
<p>
Password based authentication is not allowed for this site.
<p>
</div>
<% }else if( ! password_auth.canResetPassword(null) ){ %>
<div class="block">
<H1>Password resets disabled</H1>
<p>
Password resets are not allowed for this site.
<p>
</div>
<% }else{ %>
<div class="block" role="main">
<h1><%=page_title %></h1>
<p> If you already have an account for the <%=website_name %> but can't
remember your password, you can use this form to send yourself a password recovery email.
</p>
<form method="post" action="<%= response.encodeURL(web_path+"/LoginServlet") %>">
   <table class="form">
	<tr>
		<td><label for="name" class="required"><%=fac.getNameLabel() %></label></td>
		<td><input id="name" type="text" class="input" name="username" required/></td>
	</tr>
	</table>
	<fieldset class="action_buttons">
	<input type="submit" name="email_password" value="Send password recovery email"
			class="input_button" />
	<a href="<%=web_path%>/login.jsp">Cancel</a>
	</fieldset>
</form>
</div>
<% } %>
<%@ include file="/login_footer.jsf"%>