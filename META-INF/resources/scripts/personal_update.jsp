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
Update personal details

--%>
<%@ page	
   import="java.util.*, uk.ac.ed.epcc.webapp.*,  uk.ac.ed.epcc.webapp.model.*,uk.ac.ed.epcc.webapp.forms.html.*,uk.ac.ed.epcc.webapp.forms.factory.*,uk.ac.ed.epcc.webapp.servlet.navigation.NavigationMenuService"
%>
<%@ include file="/session.jsf" %>
<%
	AppUser user = session_service.getCurrentPerson();
	String page_title = service_name + " Update Personal Details";
	if( user.needDetailsUpdate()){
		// Need to complete
		request.setAttribute(NavigationMenuService.DISABLE_NAVIGATION_ATTR, Boolean.TRUE);
	}
%>
<%@ include file="/std_header.jsf" %>
<%@page import="uk.ac.ed.epcc.webapp.forms.*" %>
<%
	AppUserFactory fac = conn.getService(SessionService.class).getLoginFactory();
    StandAloneFormUpdate u = (StandAloneFormUpdate) fac.getFormUpdate(conn);
   HTMLForm form = new HTMLForm(conn);
   u.buildUpdateForm("Person",form,session_service.getCurrentPerson(),session_service);
   boolean multi = form.containsInput(FileInput.class);
%>
<%@ include file="/scripts/form_context.jsf" %>

<div class="block" role="main">
<h2>This page is to allow you to update your contact details.</h2>

<h3>Your current details:</h3>
  <form method="post" 
<% if( multi ){ %>
   enctype="multipart/form-data"
<% } %>  
  action="<%= response.encodeURL(web_path+"/UserServlet") %>">
	<input type="hidden" name="form_url" value="/personal_update.jsp"/>
	<input type="hidden" name="action" value="MODIFY_PERSON"/>

          <table class="form">
          <%= form.getHtmlFieldTable(request) %>
          </table>
	    <input class="input_button" type="submit" value=" Commit Update "/>
  </form>
</div>
<%@ include file="/std_footer.jsf" %>