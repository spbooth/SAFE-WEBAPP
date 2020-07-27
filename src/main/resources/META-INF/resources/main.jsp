<%@ taglib uri="http://safe.epcc.ed.ac.uk/webapp" prefix="wb" %>
<wb:ServiceInit/>
<wb:session/>
<%
	String page_title = service_name+" Main";
%>
<%@ include file="std_header.jsf"%>
<div class="block">
<h1>Default main page</h1>
<p> This is a placeholder main page for the webapp framework.
If you are seeing this then then either:
<ol>
<li>The application is mis-configured</li>
<li>The application is under construction and a main page is not written yet.</li>
</ol>
</p>
</div>

<%@ include file="std_footer.jsf"%>
