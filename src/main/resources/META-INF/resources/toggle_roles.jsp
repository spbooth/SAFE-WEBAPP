<%@ taglib uri="http://safe.epcc.ed.ac.uk/webapp" prefix="wb" %>
<wb:ServiceInit/>
<wb:session/>
<%-- A landing page for the role toggle menu --%>
<%
String page_title="Toggle Roles";
%>
<%@ include file="std_header.jsf" %>
<wb:RoleButtons/>
<%@ include file="std_footer.jsf" %>