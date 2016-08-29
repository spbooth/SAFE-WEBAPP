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
<%@ page	session="false"
   import="java.util.*, uk.ac.ed.epcc.webapp.*, uk.ac.ed.epcc.webapp.model.*,uk.ac.ed.epcc.webapp.forms.html.*,uk.ac.ed.epcc.webapp.session.AppUserFactory"
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
	//AppContext conn = (AppContext) new ServletAppContext(this);
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
%>
<%@ include file="/std_header.jsf" %>


<div class="block">
<H2>New Person Signup Form</H2>


<p>

This is the <%=service_name%> Administration Service.  
If you already have a password for this website:
<b>
 <a href="<%= response.encodeURL(web_path+"/login.jsp") %>">Go to Login</a></b>
 
</p>
</div>


<%
     AppUserFactory person_fac =  session_service.getLoginFactory();
     //PasswordAuthAppUserFactory<?> person_fac = new PasswordPersonFactory(conn,"Person");
     // Normally webname will be null here but might as well check
     HTMLCreationForm creator = new HTMLCreationForm("Signup",person_fac.getSignupFormCreator(RegisterServlet.getRealm(conn),conn.getService(ServletService.class).getWebName()));
%>
<div class="block" role="main">
<%@ include file="/scripts/form_context.jsf" %>
  <h2>Your Details:</h2>
  <p>Fields marked in <b>bold</b> are mandatory.</p>
  <%= person_fac.addUpdateNotes(new HtmlBuilder()) %>
  <form method="post" action="<%= response.encodeURL(web_path+"/SignupServlet") %>">
     <input type="hidden" name="form_url" value="<%=HTMLForm.getFormURL(request)%>">
     
     <%= creator.getHtmlForm(request) %>
      
      <div class="action_buttons">
      <input class="input_button" type="reset" value="   Clear   "/>
      </div>
  </form>
</div>

<%@ include file="/std_footer.jsf" %>