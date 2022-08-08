<%@page import="uk.ac.ed.epcc.webapp.session.NotifiableContentProvider"%>
<%@page import="uk.ac.ed.epcc.webapp.tags.WebappHeadTag" %>
<%@ taglib uri="http://safe.epcc.ed.ac.uk/webapp" prefix="wb" %>
<wb:ServiceInit/>
<wb:session role="Admin"/>
<%	
String page_title = service_name+" user notifications";
WebappHeadTag.addScript(conn, request, "${jquery.script}");
WebappHeadTag.addScript(conn, request, "${datatables.script}");
WebappHeadTag.addCss(conn, request, "${datatables.css}");
WebappHeadTag.addScript(conn, request, "${colVis.script}");
WebappHeadTag.addCss(conn, request, "${colVis.css}");
WebappHeadTag.addScript(conn, request, "${colReorder.script}");
WebappHeadTag.addCss(conn, request, "${colReorder.css}");
WebappHeadTag.addScript(conn, request,
		"/js/appuser_datatable.js");

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
<%@page import="uk.ac.ed.epcc.webapp.content.Table" %>
<%@page import="uk.ac.ed.epcc.webapp.session.NotifiableContentProvider" %>
<%
HtmlBuilder hb = new HtmlBuilder();
NotifiableContentProvider ncp = new NotifiableContentProvider(conn);
Table t = ncp.getTable();
if( t.hasData()){
   HtmlBuilder wrapper=(HtmlBuilder)hb.getPanel("scrollwrapper");

	wrapper.setTableSections(true);
	t.setId("datatable");
	wrapper.addTable(conn, t,"display");
	wrapper.addParent();
    %><%= hb %><%
}
%>

</div>
<%@ include file="back.jsf" %>
<%@ include file="std_footer.jsf" %>
