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
<%-- Links to become User
    
	
--%>
<%@page import="uk.ac.ed.epcc.webapp.jdbc.WrappedDatabaseService"%>
<%@ include file="/session.jsf" %>
<%@page import="uk.ac.ed.epcc.webapp.servlet.session.ServletSessionService" %>
<%	
	/* Must be an admin */
	if( !((ServletSessionService)session_service).hasRole(SessionService.ADMIN_ROLE)) {
%>
	<jsp:forward page="/messages.jsp?message_type=access_denied" />
<%	
     return;
	}
%>
<%	
	String page_title = " View active database connections ";
%>
<%@ include file="/std_header.jsf" %>
<br>
<%@ include file="/main__logged_in.jsf" %>
<br>
<%@ include file="/back.jsf" %>
<br>
<%@page import="uk.ac.ed.epcc.webapp.content.*" %>
<%@page import="uk.ac.ed.epcc.webapp.jdbc.DatabaseService" %>
<div class="block">
<%if ( ErrorFilter.CONNECTION_STATUS_FEATURE.isEnabled(conn)){ %>
<h1>Database connections</h1>
<% HtmlBuilder builder = new HtmlBuilder();
builder.addTable(conn, WrappedDatabaseService.getStatusTable());
%>
<%= builder.toString() %>
</div>	
<%} %>
<div class="block">
<h1>Current connections</h1>
<% HtmlBuilder builder2 = new HtmlBuilder();
DatabaseService s = conn.getService(DatabaseService.class);
Table t = new Table();
t.addMap("Attributes",s.getConnectionAttributes());
t.setKeyName("Attribute");
builder2.addColumn(conn, t, "Attributes");
%>
<%= builder2.toString() %>
</div>	
<br/>
<%@ include file="/back.jsf" %>
<%@ include file="/std_footer.jsf" %>