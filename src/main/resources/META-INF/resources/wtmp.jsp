<%@ taglib uri="http://safe.epcc.ed.ac.uk/webapp" prefix="wb" %>
<wb:session role="wtmp,Admin"/>
<%@page import="uk.ac.ed.epcc.webapp.AppContext" %>
<%@page import="uk.ac.ed.epcc.webapp.servlet.ErrorFilter" %>
<%
  	AppContext conn = ErrorFilter.retrieveAppContext(request,response);
	String web_path = request.getContextPath();
	// path of the diretory above the css dir
	String template_path = "";	
	String service_name  = "";
	String website_name = "";
	// error-pages do NOT go throught he filter first so we may have a null context here
	if( conn != null ){
		template_path = request.getContextPath()+conn.getInitParameter("template.path","");	
		service_name = conn.getInitParameter("service.name","");
		website_name = conn.getInitParameter("service.website-name","");
	}
%>
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
