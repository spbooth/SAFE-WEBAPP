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
signup.jsp - Page used to sign up for an account using password auth.

  Arguments:
	none

  Links:
	SignupServlet POST - Validates details and creates database records if appropriate

--%>
<%@ page import="java.util.*, uk.ac.ed.epcc.webapp.*, uk.ac.ed.epcc.webapp.model.*,uk.ac.ed.epcc.webapp.forms.html.*,uk.ac.ed.epcc.webapp.session.AppUserFactory"
%>
<%@page session="false" %>
<%@ include file="/service_init.jsf" %>
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
   /* Retrieve obj for person currently logged in - 
    * if an error is returned, return to login page. */
    SessionService<?> session_service = conn.getService(SessionService.class);
    if(session_service != null){
	  AppUser person = session_service.getCurrentPerson();
      if(person != null ) {
%>
		  <jsp:forward page="/main.jsp" />
<%	
	     return;
      }
   }

	
	String page_title = service_name+" "+website_name+" Signup";
	String privacy_policy=conn.getInitParameter("service.url.privacypolicy");
%>
<%@ include file="/std_header.jsf" %>

<script language="javascript">
function open_privacy_popup() {
	window.open("<%= privacy_policy %>", "singup_privacy_popup_window", "scrollbars=1, toolbar=0, resizable=1, height=440, width=400");
	return false;
}
</script>

<div class="block">
<H2><%=page_title%></H2>
<p class="highlight">
This is the <%=service_name%> <%=website_name%>.  
</p>
</div>


<%@ include file="/scripts/form_context.jsf" %>
<%
String default_charset = conn.getService(ServletService.class).defaultCharset();
AppUserFactory person_fac =  session_service.getLoginFactory();
HTMLCreationForm creator = new HTMLCreationForm("Signup",person_fac.getSignupFormCreator(RegisterServlet.getRealm(conn),conn.getService(ServletService.class).getWebName()));
boolean multi = creator.useMultiPart();
%>
<div class="block" role="main">
<h2>Registration form</h2>
<p>Fields marked in <b>bold</b> are mandatory.</p>
<%= person_fac.addUpdateNotes(new HtmlBuilder()) %>
<% if( privacy_policy != null ){ %>
<p>
<small>All information supplied is held and processed in accordance with the <%=service_name%> Personal Data and Privacy Policy.
	  You can find full details <a href="<%= privacy_policy%>" onClick="return open_privacy_popup();"><b>here</b></a>.</small>
</p>
<% } %>
  <form method="post" 
<% if( multi ){ %>
   enctype="multipart/form-data"
<% } %>  
<% 
if( default_charset != null && ! default_charset.isEmpty()){
%> accept-charset="<%=default_charset %>"
<% } %>
  autocomplete="on"
  action="<%= response.encodeURL(web_path+"/SignupServlet") %>">
<input type="hidden" name="form_url" value="<%=HTMLForm.getFormURL(request)%>">
<%= creator.getHtmlForm(request) %>
  </form>
</div>

<%@ include file="/std_footer.jsf" %>