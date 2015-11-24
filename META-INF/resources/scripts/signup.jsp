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

	
	String page_title = service_name+" Web-site Signup";
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
     HTMLCreationForm creator = new HTMLCreationForm("Signup",person_fac.getSignupFormCreator(conn.getService(ServletService.class).getWebName()));
%>
<div class="block">
<%@ include file="/scripts/form_context.jsf" %>
  <h2>Your Details:</h2>
  <%= person_fac.addUpdateNotes(new HtmlBuilder()) %>
  <form method="post" action="<%= response.encodeURL(web_path+"/SignupServlet") %>">
     <input type="hidden" name="form_url" value="<%=HTMLForm.getFormURL(request)%>">
      <table class="form">
        <%= creator.getHtmlForm(request) %>
      </table>
      <div class="action_buttons">
      <input class="input_button" type="reset" value="   Clear   "/>
      </div>
  </form>
</div>

<%@ include file="/std_footer.jsf" %>

