<%--
A generic message page used for printing standardised messages

--%>
<%@ page
	import="uk.ac.ed.epcc.webapp.*, uk.ac.ed.epcc.webapp.model.*, java.util.* "%>
<%@ include file="/service_init.jsf"%>
<%
    String page_title = service_name+" Information";
%>
<%@ include file="/std_header.jsf"%>
<%@ include file="/back.jsf" %>
<%@ include file="/scripts/messages.jsf" %>

<%@ include file="/std_footer.jsf"%>
