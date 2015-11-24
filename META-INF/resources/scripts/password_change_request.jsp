<%-- 
    password_update.jsp - Page used to change password.


   
--%>
<%@page import="uk.ac.ed.epcc.webapp.servlet.navigation.NavigationMenuService"%>
<%@ page	
   import="uk.ac.ed.epcc.webapp.*, uk.ac.ed.epcc.webapp.model.*, uk.ac.ed.epcc.webapp.session.*,uk.ac.ed.epcc.webapp.forms.html.*"
%>
<%@ include file="/service_init.jsf" %>
<%	
	String page_title = service_name+" Change Password";
	PageHTMLForm form = (PageHTMLForm) request.getAttribute("Form");
%>
<%if( form == null ){ %>
<jsp:forward page="/messages.jsp?message_type=access_denied" />
<%} %>
<%@ include file="/std_header.jsf" %>

<%@ include file="/scripts/form_context.jsf" %>
<div class="block">
<h2>Please set a password</h2>
<% if( form.hasSubmitted() && form.hasError()){ %>
<h3>This form contains errors:</h3>
<p class="warn">	
<%= form.getGeneralError() %>
</p>
<%} %>
<form>
<%= form.getHtmlForm() %>
<div class="action_buttons">
<input type="submit" value="Set Password"/>
</div>
</form>
</div>


<%@ include file="/std_footer.jsf" %>
