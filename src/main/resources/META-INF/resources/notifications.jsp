<%@page import="uk.ac.ed.epcc.webapp.session.NotifiableContentProvider"%>
<%@ taglib uri="http://safe.epcc.ed.ac.uk/webapp" prefix="wb" %>
<wb:ServiceInit/>
<wb:session role="Admin"/>
<%	
String page_title = service_name+" user norifications";
%>
<%@ include file="std_header.jsf" %>
<br>
<%@ include file="main__logged_in.jsf"%>
<br>
<%@ include file="back.jsf" %>
<div class="block">
<h1>Required page notifications</h1>
<p>These are the notifications that would be sent to users
if email notifications of required pages are enabled.
</p>
<%@page import="uk.ac.ed.epcc.webapp.content.HtmlBuilder" %>
<%@page import="uk.ac.ed.epcc.webapp.session.NotifiableContentProvider" %>
<%= (new  NotifiableContentProvider(conn)).addContent(new HtmlBuilder()) %>
</div>
<%@ include file="back.jsf" %>
<%@ include file="std_footer.jsf" %>
