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
<%
Map<String,Class> map = conn.getClassMap(TableTransitionTarget.class);
TableTransitionProvider provider = new TableTransitionProvider(conn);
for(String table : map.keySet()){ 
%>
<div class='block'>
<h3><%=table %></h3>
<% 
    Class clazz = map.get(table);
	if(clazz != null ){
		TableTransitionTarget target = (TableTransitionTarget) conn.makeObject(clazz, table);
%>
	<h4>Table Type: <%=clazz.getSimpleName() %></h4>
	<form action="<%=response.encodeURL(request.getContextPath()+TransitionServlet.getURL(conn, provider, target))%>" method="post">
	<div class="action_buttons">
	<input type="submit" value="Administer Table"/>
	</div>
	</form>
<%  }else{ %>
<p>No administration operations</p>
<%  } %>
</div>
<%} %>

<%@ include file="/std_footer.jsf"%>