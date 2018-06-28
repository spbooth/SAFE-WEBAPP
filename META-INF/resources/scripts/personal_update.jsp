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
<%--Must be only basic_session as this may be a required page --%>
<%@ include file="/basic_session.jsf" %>
<%
	AppUser user = session_service.getCurrentPerson();
	String page_title = service_name + " Update Personal Details";
    boolean update_required=user.needDetailsUpdate();
	if( update_required){
		// Need to complete
		request.setAttribute(NavigationMenuService.DISABLE_NAVIGATION_ATTR, Boolean.TRUE);
	}
%>
<wb:formpage/>
<%@ include file="/std_header.jsf" %>
<%@page import="uk.ac.ed.epcc.webapp.forms.*" %>
<%
    String default_charset = conn.getService(ServletService.class).defaultCharset();
	AppUserFactory fac = conn.getService(SessionService.class).getLoginFactory();
    StandAloneFormUpdate u = (StandAloneFormUpdate) fac.getFormUpdate(conn);
   HTMLForm form = new HTMLForm(conn);
   u.buildUpdateForm("Person",form,session_service.getCurrentPerson(),session_service);
   Date last_update = user.getLastTimeDetailsUpdated();
   DateFormat df = DateFormat.getDateInstance();
   boolean multi = form.containsInput(FileInput.class);
%>
<%if(update_required){ 
%>
<div class="block">
<h2>Update required</h2>
<wb:content optional="true" message="person_update_required"/>
</div>
<%}%>
<div class="block">
<h2>This page is to allow you to update your contact details.</h2>
<%= fac.addUpdateNotes(new HtmlBuilder(),user) %>
<%@ include file="/scripts/privacy_policy.jsf" %>
</div>

<%@ include file="/scripts/form_context.jsf" %>
<div class="block" role="main">
<h3>Your current details:</h3>
<% 
if( last_update != null ){
%><p>Last updated: <%=df.format(last_update) %></p><%
}
%>
<form method="post" 
<% if( multi ){ %>
   enctype="multipart/form-data"
<% } %> 
<% 
if( default_charset != null && ! default_charset.isEmpty()){
%> accept-charset="<%=default_charset %>"
<% } %>
  autocomplete="on" 
  action="<%= response.encodeURL(web_path+"/UserServlet") %>">
	<input type="hidden" name="form_url" value="/personal_update.jsp"/>
	<input type="hidden" name="action" value="MODIFY_PERSON"/>
	<% HtmlBuilder result = new HtmlBuilder();
	result.setFormID("update");
	if(HTMLForm.hasError(request)){
		// show errors
		result = form.getHtmlFieldTable(result, request);
	}else{
		// validate current state
		result = form.getHtmlFieldTable(result);
	}
	%>
    <%= result.toString() %>
    <div class="action_buttons">
	<input class="input_button" type="submit" value=" Update "/>
	</div>
  </form>
</div>
<%@ include file="/std_footer.jsf" %>