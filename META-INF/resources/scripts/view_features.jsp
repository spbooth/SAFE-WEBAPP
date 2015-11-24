<%-- Links to become User
    
	
--%>
<%@ include file="/session.jsf" %>
<%@page import="uk.ac.ed.epcc.webapp.servlet.session.ServletSessionService" %>
<%	
	/* Must be an admin */
	if( !((ServletSessionService)session_service).hasRole(SessionService.ADMIN_ROLE)) {
%>
	<jsp:forward page="/messages.jsp?message_type=access_denied" />
<%	
     return;
	}
%>
<%	
	String page_title = " View configured features ";
%>
<%@ include file="/std_header.jsf" %>
<br>
<%@ include file="/main__logged_in.jsf" %>
<br>
<%@ include file="/back.jsf" %>
<br>
<%@page import="uk.ac.ed.epcc.webapp.content.*" %>
<div class="block">
<h2>Configuration features</h2>
<p>This is the list of current configurable features. The list is populated as
features are referenced so may be incomplete when the server first starts.</p>
<% HtmlBuilder builder = new HtmlBuilder();
builder.addTable(conn, Feature.getFeatureTable(conn));
%>
<%= builder.toString() %>
</div>	

<br/>
<%@ include file="/back.jsf" %>
<%@ include file="/std_footer.jsf" %>