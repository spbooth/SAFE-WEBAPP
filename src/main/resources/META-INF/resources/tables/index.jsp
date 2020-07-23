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
<%@ page
	import="uk.ac.ed.epcc.webapp.*,uk.ac.ed.epcc.webapp.jdbc.table.*,uk.ac.ed.epcc.webapp.model.*"%>
<%@ include file="/session.jsf"%>
<%
	String page_title = service_name+" Table Administration"; 
%>
<%@ include file="/std_header.jsf"%>
<%@ include file="/main__logged_in.jsf" %>
<div class='block'>
<h2>Table Administration</h2>
<p>This page lists the current tables in the accounting database and gives access to the built-in administration operations for each table</p>
</div>
<%@ page import="uk.ac.ed.epcc.webapp.model.data.DataObjectFactory" %>
<%
Map<String,Class> map = conn.getClassMap(DataObjectFactory.class);
TableTransitionProvider provider = new TableTransitionProvider(conn);
for(String table : map.keySet()){ 
%>
<div class='block'>
<h3><%=table %></h3>
<% 
    Class clazz = map.get(table);
	if(clazz != null ){
		DataObjectFactory target = (DataObjectFactory) conn.makeObject(clazz, table);
%>
	<h4>Table Type: <%=clazz.getSimpleName() %></h4>
	<form action="<%=response.encodeURL(request.getContextPath()+TransitionServlet.getURL(conn, provider, target))%>" method="post">
	<div class="action_buttons">
	<input class="input_button" type="submit" value="Administer Table"/>
	</div>
	</form>
<%  }else{ %>
<p>No administration operations</p>
<%  } %>
</div>
<%} %>

<%@ include file="/std_footer.jsf"%>