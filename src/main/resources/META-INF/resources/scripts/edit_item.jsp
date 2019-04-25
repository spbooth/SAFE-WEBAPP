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
<%--
Page to edit a LogItem
--%>
<%@ page import="uk.ac.ed.epcc.webapp.model.log.*,uk.ac.ed.epcc.webapp.forms.html.*" %>
<%@ include file="/session.jsf" %>
<wb:css url="service_desk.css"/>
<%
// This page must be invoked from LogItemServlet
LogFactory.Entry item = (LogFactory.Entry) request.getAttribute("Item");
if( item == null ){
%>
	<jsp:forward page="/messages.jsp?message_type=access_denied" />
<%	
     return;	
}	
%>
<%	
	String page_title = service_name+"Edit Item";
%>
<wb:formpage/>
<%@ include file="/std_header.jsf" %>
<%@ include file="/main__logged_in.jsf" %>

<div class="block">
<%
HTMLForm f = new HTMLForm(conn);
item.buildUpdateForm(f,session_service);
f.setContents(item.getMap());
%>
<form method='post'>
<%= f.getHtmlFieldTable(request) %>
<input type='hidden' name='action' value='Update' />
<input type='hidden' name='direct' value='true' />
<%= f.getActionButtons() %>
</form>
</div>



<%@ include file="/std_footer.jsf" %>