<%--
Update personal details

--%>
<%@ page	
   import="java.util.*, uk.ac.ed.epcc.webapp.*,  uk.ac.ed.epcc.webapp.model.*,uk.ac.ed.epcc.webapp.forms.html.*,uk.ac.ed.epcc.webapp.forms.factory.*,uk.ac.ed.epcc.webapp.servlet.navigation.NavigationMenuService"
%>
<%@ include file="/session.jsf" %>
<%
	AppUser user = session_service.getCurrentPerson();
	String page_title = service_name + " Update Personal Details";
	if( user.needDetailsUpdate()){
		// Need to complete
		request.setAttribute(NavigationMenuService.DISABLE_NAVIGATION_ATTR, Boolean.TRUE);
	}
%>
<%@ include file="/std_header.jsf" %>
<%@page import="uk.ac.ed.epcc.webapp.forms.*" %>
<%
	AppUserFactory fac = conn.getService(SessionService.class).getLoginFactory();
    StandAloneFormUpdate u = (StandAloneFormUpdate) fac.getFormUpdate(conn);
   HTMLForm form = new HTMLForm(conn);
   u.buildUpdateForm("Person",form,session_service.getCurrentPerson(),session_service);
   boolean multi = form.containsInput(FileInput.class);
%>
<%@ include file="/scripts/form_context.jsf" %>

<div class="block">
<h2>This page is to allow you to update your contact details.</h2>

<h3>Your current details:</h3>
  <form method="post" 
<% if( multi ){ %>
   enctype="multipart/form-data"
<% } %>  
  action="<%= response.encodeURL(web_path+"/UserServlet") %>">
	<input type="hidden" name="form_url" value="/personal_update.jsp"/>
	<input type="hidden" name="action" value="MODIFY_PERSON"/>

          <table class="form">
          <%= form.getHtmlFieldTable(request) %>
          </table>
	    <input class="input_button" type="submit" value=" Commit Update "/>
  </form>
</div>
<%@ include file="/std_footer.jsf" %>
