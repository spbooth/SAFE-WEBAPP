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
  login.jsp - The main login page for service users are directed here when not authenticated
              with the system.
              When using external auth this is used to register first time visitors unless the
              factory is set up to automatically create entries on the fly.
              (They Must be already authenticated by the container)
              When using password auth this presents the login page and gives an option to self-register.
              
              We can support a combination of password and optional external auth by using a RegisterServlet
              and the web_login service feature.


  Note:
  Set session=false to avoid dropping a cookie until the user has indicated consent by logging in 
--%>
<%@ page session="false"
	import="uk.ac.ed.epcc.webapp.*, uk.ac.ed.epcc.webapp.model.*,uk.ac.ed.epcc.webapp.forms.html.*,java.util.*"%>
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
    log.debug("In login.jsp");
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
	}
	if(serv != null && serv.haveCurrentUser()) {
		 log.debug("We have a user");
%>
<jsp:forward page="/main.jsp" />
<%	
		return;
	}
	log.debug("Showing login page");
	String page_title = service_name+" "+website_name+" Login";
%>
<wb:formpage/>
<%@ include file="/std_header.jsf"%>
<%
    PasswordAuthComposite password_auth = fac.getComposite(PasswordAuthComposite.class);
	if( password_auth == null ){
      String webname = conn.getService(ServletService.class).getWebName(); 
      if( webname==null || webname.trim().length() == 0  ){ %>
<div class="block">
<H2>User not authenticated</H2>
<p>
Please authenticate with the web-server before registering with this service.
<p>
</div>
<%}else if(! RegisterServlet.ALLOW_SIGNUPS.isEnabled(conn)){ %>
<div class="block">
<H2>User <%=webname %> not registered</H2>
<p>
This service is only available to pre-registered users.
<p>
</div>
<%}else{ %>

<div class="block" role="main">
<h2>Register <%=webname %></h2>
<p>Welcome to the <%=service_name%> <%=website_name%>.</p>
<p> Please fill in this form to register with this service</p>
</div>

<%
     
     HTMLCreationForm person_form = new HTMLCreationForm("Person",fac.getSignupFormCreator(RegisterServlet.getRealm(conn),webname));
%>
<div class="block">
<%@ include file="/scripts/form_context.jsf" %>
  <h2>Your Details:</h2>
  <form method="post" action="<%= response.encodeURL(web_path+"/RegisterServlet") %>">
   <input type="hidden" name="action" value="REGISTER">
   <input type="hidden" name="form_url" value="<%=HTMLForm.getFormURL(request)%>">
      <table class="form">
        <%= person_form.getHtmlForm(request) %>
      </table>
  </form>
</div>


<% }
   }else{ 
      // offer password based login   
%>
<div class="block" role="main">
<h2><%=page_title %></h2>
<%=conn.getExpandedProperty("login.welcome","Welcome to ${service.name}")%>
<%=conn.getExpandedProperty("login.link","")%>
<% if("login".equals(request.getParameter("error"))) { %>
<h3>Incorrect <%=fac.getNameLabel()%> or Password</h3>
<p><b>please check your details and try again</b> <br />
<small>If you have forgotten your password, enter your Email address and
click <b>Email</b> and we will send you a new password for the <%=website_name %>.</small> <br />
</p>
<% } %> <% if("session".equals(request.getParameter("error"))) { %>
<h3>Session invalid or expired</h3>
<p><b>please login again</b></p>
<% } %> <% if("database".equals(request.getParameter("error"))) { %>
<h3>Database error</h3>
<p><b>please try again later</b></p>
<% } %>
<form method="post" action="<%= response.encodeURL(web_path+"/LoginServlet") %>"><%
	String prev_page = (String) request.getAttribute("page");
    if( prev_page == null ){
		prev_page = request.getParameter("page");
    }
	if("session".equals(request.getParameter("error")) && (prev_page!=null)) {
	%>
   <input type="hidden" name="page" value="<%= prev_page %>" /> <% } %>
   <table class="form">
	<tr>
		<td><b><%=fac.getNameLabel() %>:</b></td>
		<td><input type="text" class="input" name="username" /></td>
		<td>&nbsp;</td>
	</tr>
	<tr>

		<td><b>Password:</b></td>
		<td><input type="password" class="input" name="password" /></td>
		<td><input class="input_button" type="submit" value="Login" /></td>
	</tr>
	<tr>
		<td colspan="3">&nbsp;</td>
	</tr>
<% if( password_auth.canResetPassword(null) ){ %>
	<tr>
		<td colspan="2"><b>Request a new website password:</b></td>
		<td><input type="submit" name="email_password" value="Email"
			class="input_button" /></td>
	</tr>
<% } %>
  </table>
</form>
<% 
// Give urls for alternate external auth login. Normally just the one but
// can use comma seperated list to support multiple types.
String login_urls = conn.getInitParameter("service.web_login.url");
if(  login_urls != null ){ 
 String urls[] = login_urls.trim().split("\\s*,\\s*");
 String labels[] = conn.getInitParameter("service.web_login.login-text","Alternate login").split("\\s*,\\s*");
 for( int i = 0 ; i < urls.length ; i++){
%>
<form method="get" action="<%=web_path+urls[i] %>">
<table class="form">
<tr>
<td><b><%=labels[i%labels.length] %></b></td>
<td><input class="input_button" type="submit" value="Go" /></td>
</tr>
</table>
</form>
<%
 }
} 
%>
<p>
<small><small>As part of its normal functioning when you log in the <%=website_name %> will install a temporary session cookie that will be removed when you log off or close your browser. If you do not wish this cookie 
to be set, disable cookies in your browser settings.</small></small>
</p>
</div>
<% if( RegisterServlet.ALLOW_SIGNUPS.isEnabled(conn)){ %>
<div class="block">
<p>To create an account on the <%=website_name %> click <a
	href='<%= response.encodeURL(web_path+"/signup.jsp") %>'><b>here</b></a>.
</p>
</div>
<% } %>
<% } %>
<%@ include file="/login_footer.jsf"%>