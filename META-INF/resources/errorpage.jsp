<%-- 
errorpage.jsp - Page used to trap and process servlet exceptions and errors

  Arguments:
	none

  Links:

Note make sure that this page does not actually output any whitepace 
otherwise the redirect will fail

--%>
<%@ page	
   import="uk.ac.ed.epcc.webapp.*"
%>
<%@ include file="/service_init.jsf"%>
<% String page_title = "Errorpage"; %>
<%@ include file="/std_header.jsf"%>
<%@ include file="/scripts/errorpage.jsf" %>
<%@ include file="/std_footer.jsf"%>
