<%--
 A generic yes/no dialog box
 Any parameters to this page are automatically forwarded to the response page to
 make it easy to insert this into any form post operation.
 
The current plan is that the target servlet should check for a "confirm" parameter and redirect to
the dialog box if missing.

--%>
<%@ include file="/scripts/service_init.jsf"%>
<%
    String 	page_title="Confirm Request";
%>
<%@ include file="/std_header.jsf"%>

<%@ include file="/scripts/confirm.jsf"%>

<%@ include file="/std_footer.jsf"%>

