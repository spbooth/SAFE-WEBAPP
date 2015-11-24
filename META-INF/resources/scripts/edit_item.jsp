<%--
Page to edit a LogItem
--%>
<%@ page import="uk.ac.ed.epcc.webapp.model.log.*,uk.ac.ed.epcc.webapp.forms.html.*" %>
<%@ include file="/session.jsf" %>
<% extra_css="service_desk.css"; %>
<%
// This page must be invoked from LogItemServlet
LogFactory.Entry item = (LogFactory.Entry) request.getAttribute("Item");
if( item == null ){
%>
	<jsp:forward page="/messages.jsp?message_type=access_denied" />
<%	
     return;	
}	
%>
<%	
	String page_title = service_name+"Edit Item";
%>
<%@ include file="/std_header.jsf" %>
<%@ include file="/main__logged_in.jsf" %>

<div class="block">
<%
HTMLForm f = new HTMLForm(conn);
item.buildUpdateForm(f,session_service);
f.setContents(item.getMap());
%>
<form method='post'>
<%= f.getHtmlFieldTable(request) %>
<input type='hidden' name='action' value='Update' />
<input type='hidden' name='direct' value='true' />
<%= f.getActionButtons() %>
</form>
</div>



<%@ include file="/std_footer.jsf" %>
