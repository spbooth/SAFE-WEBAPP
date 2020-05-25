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
<%@ page session="false" import="uk.ac.ed.epcc.webapp.*" %>
<%@ page import="uk.ac.ed.epcc.webapp.model.*" %>
<%@ page import="uk.ac.ed.epcc.webapp.forms.html.*,java.util.*,uk.ac.ed.epcc.webapp.session.*"%>
<%@ page import="uk.ac.ed.epcc.webapp.content.*" %>
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
	//log.debug("Login serv is "+serv);
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
    boolean use_reset_page = LoginServlet.RESET_PASSWORD_PAGE.isEnabled(conn);
	if( password_auth == null ){
      String webname = conn.getService(ServletService.class).getWebName(); 
      if( webname==null || webname.trim().length() == 0  ){ %>
<div class="block">
<H1>User not authenticated</H1>
<p>
Please authenticate with the web-server before registering with this service.
<p>
</div>
<%}else if(! RegisterServlet.ALLOW_SIGNUPS.isEnabled(conn)){ %>
<div class="block">
<H1>User <%=webname %> not registered</H1>
<p>
This service is only available to pre-registered users.
<p>
</div>
<%}else{ %>

<div class="block" role="main">
<h1>Register <%=webname %></h1>
<p>Welcome to the <%=service_name%> <%=website_name%>.</p>
<p> Please fill in this form to register with this service</p>
</div>

<%
     
     HTMLCreationForm person_form = new HTMLCreationForm("Person",fac.getSignupFormCreator(RegisterServlet.getRealm(conn),webname));
%>
<div class="block">
<%@ include file="/scripts/form_context.jsf" %>
  <h1>Your Details:</h1>
  <form method="post" action="<%= web_path %>/RegisterServlet">
   <input type="hidden" name="action" value="REGISTER">
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
<h1><%=page_title %></h1>
<%=conn.getExpandedProperty("login.welcome","Welcome to ${service.name}")%>
<%=conn.getExpandedProperty("login.link","")%>
<% if("login".equals(request.getParameter("error"))) { %>
<h3>Incorrect <%=fac.getNameLabel()%> or Password</h3>
<p><b>please check your details and try again</b>
</p>
<% } %> <% if("session".equals(request.getParameter("error"))) { %>
<h3>Session invalid or expired</h3>
<p><b>please login again</b></p>
<% } %> <% if("database".equals(request.getParameter("error"))) { %>
<h3>Database error</h3>
<p><b>please try again later</b></p>
<% } %>
<% 


// Give urls for alternate external auth login. Normally just the one but
// can use comma seperated list to support multiple types.
String login_urls = conn.getInitParameter("service.web_login.url");
if(  login_urls != null ){ 
 String urls[] = login_urls.trim().split("\\s*,\\s*");
 String labels[] = conn.getInitParameter("service.web_login.login-text","Alternate login").split("\\s*,\\s*");
 String help[]= conn.getInitParameter("service.web_login.help-text","").split("\\s*,\\s*");
 for( int i = 0 ; i < urls.length ; i++){
	 HtmlBuilder a = new HtmlBuilder();
	 a.open("form");
	 a.addClass("button");
	 a.attr("method","post");
	 a.attr("action",web_path+"/LoginServlet");
	 a.open("input");
	 a.attr("type","hidden");
	 a.attr("name","authtype");
	 a.attr("value",Integer.toString(i));
	 a.close();
	
	 a.open("input");
	 a.addClass("input_button");
	 a.addClass("login");
	 a.attr("type","submit");
	 String tt = help[i%help.length];
	 if( tt != null && ! tt.isEmpty()){
		a.attr("title",tt);
	 }
	 a.attr("value",labels[i%labels.length]);
	 a.close();
	 a.close(); // form
%>
<%=a.toString() %>
<%
 }
%>or<%
} 
%>
<form method="post" action="<%=web_path%>/LoginServlet">
   <table class="form">
	<tr>
		<th><label class='required' for="username"><%=fac.getNameLabel() %>:</label></th>
		<td><input id="username" type="text" class="input" name="username" required/></td>
		<td></td>
	</tr>
	<tr>

		<th><label <%= use_reset_page ? "class='required'":""%> for="password">Password</label></th>
		<td><input id="password" type="password" class="input" name="password" <%= use_reset_page ? "required" :"" %>/></td>
		<td>
		    <input class="input_button login" type="submit" title="Login using password" value="Login" />
		    <% if( password_auth.canResetPassword(null) && use_reset_page){ %>
<a href="<%=web_path%>/reset_password.jsp" title="Go to password recovery page">Forgot password?</a> 
<%}%>
</td>
</tr>
<% if( password_auth.canResetPassword(null) && ! use_reset_page){ %>
	<tr>
		<td colspan="2"><b>Request a new <%=website_name %> password:</b></td>
		<td><input type="submit" name="email_password" title="Request a new password" value="Email"
			class="input_button login" /></td>
	</tr>
<% } %>
	</table>	
</form>

<% if( RegisterServlet.ALLOW_SIGNUPS.isEnabled(conn)){ %>
<a title='Go to registration/signup page' href='<%= web_path%>/signup.jsp'>Create an account</a>
<% } %>
<p>
<small><small>As part of its normal functioning when you log in the <%=website_name %> will install a temporary session cookie that will be removed when you log off or close your browser. If you do not wish this cookie 
to be set, disable cookies in your browser settings.</small></small>
</p>
<% } %>
</div>
<%
for(String name : conn.getInitParameter("login-page.extra-content","").split(",") ){
	if( name.trim().length() > 0 ){
		UIGenerator content = conn.makeObjectWithDefault(UIGenerator.class,null,name.trim());
		if(content != null ){
			HtmlBuilder html_content = new HtmlBuilder();
			content.addContent(html_content);
			if( html_content.hasContent()){
%>
<%=html_content.toString()%>
<%		
		    }
		}
	}
}
%>
<%@ include file="/login_footer.jsf"%>