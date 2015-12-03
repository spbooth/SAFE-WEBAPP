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
<%@ include file="/session.jsf" %>
<%@page import="uk.ac.ed.epcc.webapp.servlet.session.ServletSessionService" %>
<%	
	/* Must be an admin */
	if( !((ServletSessionService)session_service).canSU(null)) {
%>
	<jsp:forward page="/messages.jsp?message_type=access_denied" />
<%	
     return;
	}
%>
<%	
	String page_title = " Switch User ";
%>
<%@ include file="/std_header.jsf" %>
<br>
<%@ include file="/main__logged_in.jsf" %>
<br>
<%@ include file="/back.jsf" %>
<br>
<%@page import="uk.ac.ed.epcc.webapp.content.*" %>
<div class="block">
<h2>All Users</h2>
<ul>
<% 
AppUserFactory<?> fac = session_service.getLoginFactory();
for(AppUser u : fac.all()){
	if( !((ServletSessionService)session_service).canSU(u)) {
	
%>
<li> <a href="<%= response.encodeURL(web_path+"/BecomeUserServlet?person_id="+u.getID()) %>"><%=u.getName() %></a></li>
<%
	}
}
%>
</ul>
</div>	

<br/>
<%@ include file="/back.jsf" %>
<%@ include file="/std_footer.jsf" %>