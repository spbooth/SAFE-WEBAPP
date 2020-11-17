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
<%@ taglib uri="http://safe.epcc.ed.ac.uk/webapp" prefix="wb" %>
<wb:ServiceInit/>
<wb:session/>
<%@page import="uk.ac.ed.epcc.webapp.servlet.session.ServletSessionService" %>
<%@page import="uk.ac.ed.epcc.webapp.session.SessionService" %>
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
	String page_title = " View database lock records ";
%>
<%@ include file="../std_header.jsf" %>
<%@ include file="../main__logged_in.jsf" %>
<%@ include file="../back.jsf" %>
<%@page import="uk.ac.ed.epcc.webapp.content.*" %>
<%@page import="uk.ac.ed.epcc.webapp.jdbc.DatabaseService" %>
<%@page import="uk.ac.ed.epcc.webapp.servlet.ErrorFilter" %>
<%@page import="uk.ac.ed.epcc.webapp.model.cron.LockFactory" %>
<div class="block">
<h1>Database lock records</h1>
<% HtmlBuilder builder = new HtmlBuilder();
LockFactory fac = LockFactory.getFactory(conn);
builder.addTable(conn, fac.getTable());
%>
<%= builder.toString() %>
</div>	
<%@ include file="../back.jsf" %>
<%@ include file="../std_footer.jsf" %>