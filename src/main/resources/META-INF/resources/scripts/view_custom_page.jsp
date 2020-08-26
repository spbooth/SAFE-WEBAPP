<%--| Copyright - The University of Edinburgh 2015                             |--%>
<%--|                                                                          |--%>
<%--| Licensed under the Apache License, Version 2.0 (the "License");          |--%>
<%--| you may not use this file except in compliance with the License.         |--%>
<%--| You may obtain a copy of the License at                                  |--%>
<%--|                                                                          |--%>
<%--|    http://www.apache.org/licenses/LICENSE-2.0                            |--%>
<%--|                                                                          |--%>
<%--| Unless required by applicable law or agreed to in writing, software      |--%>
<%--| distributed under the License is distributed on an "AS IS" BASIS,        |--%>
<%--| WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. |--%>
<%--| See the License for the specific language governing permissions and      |--%>
<%--| limitations under the License.                                           |--%>
<%-- A generic page to view CustomPageResults
--%>
<%@page import="uk.ac.ed.epcc.webapp.tags.WebappHeadTag"%>
<%@ page import="uk.ac.ed.epcc.webapp.forms.html.*,uk.ac.ed.epcc.webapp.forms.*" %>
<%@ page import="uk.ac.ed.epcc.webapp.forms.result.*" %>
<%@ taglib uri="http://safe.epcc.ed.ac.uk/webapp" prefix="wb" %>
<wb:ServiceInit/>
<wb:session/>
<%
	CustomPage custom_page =(CustomPage) request.getAttribute(CustomPage.CUSTOM_PAGE_TAG);
    if(custom_page==null){
%>
<jsp:forward page="/messages.jsp?message_type=invalid_input" />
<%
       return;
    }	
String page_title=conn.expandText(custom_page.getTitle());
if( custom_page instanceof ScriptCustomPage){
	ScriptCustomPage scp = (ScriptCustomPage) custom_page;
	WebappHeadTag.addCss(conn, request, scp.getAdditionalCSS());
	WebappHeadTag.addScript(conn, request, scp.getAdditionalScript());
}

%>
<%@ include file="../std_header.jsf" %>
<%@ include file="../main__logged_in.jsf" %>
<%@ include file="../back.jsf" %>
<div class="block" role="main">
<%@page import="uk.ac.ed.epcc.webapp.content.*" %>
<% 
if( XMLContentBuilder.STREAM_BUILDER_FEATURE.isEnabled(conn)){
	custom_page.addContent(conn, new HtmlWriter(conn,out));
}else{
	HtmlBuilder hb = new HtmlBuilder();
	custom_page.addContent(conn, hb);
%>
<%=hb.toString()%>
<%} %>
</div>
<%@ include file="../std_footer.jsf" %>