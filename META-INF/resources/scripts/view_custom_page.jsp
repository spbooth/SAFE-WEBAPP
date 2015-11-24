<%-- A generic page to view CustomPageResults
--%>
<%@ page import="uk.ac.ed.epcc.webapp.forms.html.*,uk.ac.ed.epcc.webapp.forms.*" %>
<%@ page import="uk.ac.ed.epcc.webapp.forms.result.*" %>
<%@ include file="/session.jsf" %>
<%
	CustomPage custom_page =(CustomPage) request.getAttribute(CustomPage.CUSTOM_PAGE_TAG);
    if(custom_page==null){
%>
<jsp:forward page="/messages.jsp?message_type=invalid_input" />
<%
       return;
    }	
String page_title=custom_page.getTitle();
HtmlBuilder hb = new HtmlBuilder();
custom_page.addContent(conn, hb);
%>
<%@ include file="/std_header.jsf" %>
<%@ include file="/main__logged_in.jsf" %>
<div class="block">
<%=hb.toString()%>
</div>
<%@ include file="/std_footer.jsf" %>