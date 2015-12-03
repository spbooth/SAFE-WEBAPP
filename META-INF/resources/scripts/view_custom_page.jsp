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