<%-- Page to allow users to request a change in their Email Address

We only use a basic session to allow this to be invoked from a required page
details update.

--%>
<%@ include file="/basic_session.jsf" %>
<%@ page import="uk.ac.ed.epcc.webapp.forms.*,uk.ac.ed.epcc.webapp.forms.html.*" %>
<%
String page_title="Email Change Request";

%>
<%@ include file="/std_header.jsf" %>
<%@ include file="/main__logged_in.jsf"%>

<div class='block'>
<h2>Email Change Request Form</h2>
<p>This page allows you to notify this system about a change to your Email address.
The system will then send an email to the new address giving you a URL you need to visit to 
complete the request. Once this is done your email will be updated.
<em>Please note that once the change has been made you will need to provide your new email address when
logging into this site.</em>
</div>

<%@ include file="/scripts/form_context.jsf" %>
<div class='block'>
<% 
   HTMLForm f = new HTMLForm(conn);
   EmailChangeRequestFactory fac = new EmailChangeRequestFactory(session_service.getLoginFactory());
   fac.MakeRequestForm(session_service.getCurrentPerson(),f);

%>
<form method="post" 
   action="<%= response.encodeURL(web_path+"/EmailChangeRequestServlet") %>">
  <input type="hidden" name="form_url" value="<%=HTMLForm.getFormURL(request)%>" />
  <input type='hidden' name='Action' value='Request'/>
  <%=f.getHtmlFieldTable(request) %>
  <%=f.getActionButtons() %>
  </form>
</div>


<%@ include file="/back.jsf" %>
<%@ include file="/std_footer.jsf" %>