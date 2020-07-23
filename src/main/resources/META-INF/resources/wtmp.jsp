<%@ taglib uri="http://safe.epcc.ed.ac.uk/webapp" prefix="wb" %>
<wb:ServiceInit/>
<wb:session role="wtmp,Admin"/>
<%	
String page_title = service_name+" Wtmp";
%>
<%@ include file="std_header.jsf" %>
<br>
<%@ include file="main__logged_in.jsf"%>
<br>
<%@ include file="back.jsf" %>
<%@ include file="scripts/wtmp.jsf" %>
<%@ include file="back.jsf" %>
<%@ include file="std_footer.jsf" %>
