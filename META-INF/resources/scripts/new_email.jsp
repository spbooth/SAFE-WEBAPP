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
<%-- Page to allow users to request a change in their Email Address

We only use a basic session to allow this to be invoked from a required page
details update.

--%>
<%@ include file="/basic_session.jsf" %>
<%@ page import="uk.ac.ed.epcc.webapp.forms.*,uk.ac.ed.epcc.webapp.forms.html.*" %>
<wb:formpage/>
<%
String page_title="Update Email";

%>
<%@ include file="/std_header.jsf" %>
<%@ include file="/main__logged_in.jsf"%>

<div class='block'>
<h2><%=page_title %></h2>
<p><wb:content message="change_email"/>
<% 
AppUserFactory login_fac = session_service.getLoginFactory();
AppUserNameFinder realm = login_fac.getRealmFinder(EmailNameFinder.EMAIL);
if( login_fac.hasComposite(PasswordAuthComposite.class) && realm != null && realm.userVisible()){ %>
<em><wb:content message="change_email_password"/></em>
<% } %>
</div>

<%@ include file="/scripts/form_context.jsf" %>
<div class='block' role="main">
<% 
   HTMLForm f = new HTMLForm(conn);
   EmailChangeRequestFactory fac = new EmailChangeRequestFactory(session_service.getLoginFactory());
   fac.MakeRequestForm(session_service.getCurrentPerson(),f);

%>
<form method="post" 
   action="<%= response.encodeURL(web_path+"/EmailChangeRequestServlet") %>">
  <input type="hidden" name="form_url" value="<%=HTMLForm.getFormURL(request)%>" />
  <input type='hidden' name='Action' value='Update'/>
  <%=f.getHtmlFieldTable(request) %>
  <%=f.getActionButtons() %>
  </form>
</div>


<%@ include file="/back.jsf" %>
<%@ include file="/std_footer.jsf" %>