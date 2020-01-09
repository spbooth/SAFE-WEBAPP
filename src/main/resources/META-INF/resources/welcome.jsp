<%-- Placeholder welcome page
We bypass the change password check for this page otherwise the user would never see it.
This page will stop being displayed once the user resets their initial random password
--%>
<%@ include file="basic_session.jsf" %>
<%	
	String page_title = service_name+" Welcome";
%>
<%@ include file="std_header.jsf" %>
<br/>
<%@ include file="/main__logged_in.jsf" %>
<br/>

<div class="block">
<h2>Welcome to the <%=service_name%> <%=website_name %></h2>
 
 
  <form method="GET" action="<%= response.encodeURL(web_path+"/main.jsp") %>">
 
		<input class="input_button" type="submit" value="  Continue  ">
	
  </form>
</div>

<%@ include file="std_footer.jsf" %>

