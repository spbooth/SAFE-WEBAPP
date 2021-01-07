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
<%-- A generic page to view targets of a ViewTransitionProvider
This should always be invoked as a forward from the TransitionServlet.

Note that as the target and provider are encoded in the servlet-path
the form could just submit to self. 
--%>
<%@page import="uk.ac.ed.epcc.webapp.tags.WebappHeadTag"%>
<%@ page import="uk.ac.ed.epcc.webapp.content.*,uk.ac.ed.epcc.webapp.forms.html.*,uk.ac.ed.epcc.webapp.forms.*, uk.ac.ed.epcc.webapp.forms.transition.*" %>
<%@page import="uk.ac.ed.epcc.webapp.servlet.TransitionServlet" %>
<%@ taglib uri="http://safe.epcc.ed.ac.uk/webapp" prefix="wb" %>
<wb:ServiceInit/>
<wb:session/>
<wb:css url="service_desk.css"/>
<%
    TransitionFactory tp = TransitionServlet.getProvider(conn,request);
	Object target =   TransitionServlet.getTarget(conn,tp,request);
    // note key will always be null if page invoked directly.
    // As actions are performed by the servlet that does access control the only
    // risk is information leakage.
    if( tp == null || 
    (target == null && !( tp instanceof IndexTransitionFactory))|| 
    !  (tp instanceof ViewTransitionFactory)){
%>
<jsp:forward page="/messages.jsp?message_type=invalid_input" />
<%
       return;
    }
    if( target == null && tp instanceof IndexTransitionFactory){
    	//we must have been called directly
    	// get the servlet to find the default transition
    	// use redirect to ensure no looping
    	response.sendRedirect(web_path+TransitionServlet.getURL(conn, tp, null));
    	return;
    }
    ViewTransitionFactory provider = (ViewTransitionFactory) tp;
    if( ! provider.canView(target,session_service)){
%>
<jsp:forward page="/messages.jsp?message_type=access_denied" />
<%    	
    }
    String crsf = TransitionServlet.getCrsfToken(conn, request);
    if( tp instanceof ScriptTransitionFactory){
    	ScriptTransitionFactory st = (ScriptTransitionFactory)tp;
    	WebappHeadTag.addCss(conn, request, st.getAdditionalCSS(null));
    	WebappHeadTag.addScript(conn, request, st.getAdditionalScript(null));
    }
%>
<%	
String page_title="View";
if( tp instanceof TitleTransitionFactory){
	TitleTransitionFactory ttp = (TitleTransitionFactory)tp;
	page_title=ttp.getTitle(null, target);
}else{
	String type=provider.getTargetName();
    String type_title = conn.getInitParameter("transition_title."+type,type);
	page_title = service_name+" View "+type_title;
}
	TransitionServlet.recordView(session_service,provider,target);
try{
%>
<%@ include file="../std_header.jsf" %>
<%@ include file="../main__logged_in.jsf" %>
<%@ include file="../back.jsf" %>
<%= provider.getTopContent(new HtmlBuilder(),target,session_service).toString() %>
<div class="block" role="main">
<% if( XMLContentBuilder.STREAM_BUILDER_FEATURE.isEnabled(conn)){
	provider.getLogContent(new HtmlWriter(conn,out),target,session_service);
}else{	
%>
<%= provider.getLogContent(new HtmlBuilder(),target,session_service).toString() %>
<%} %>
<form class="view" id="form" action="<%=response.encodeURL(web_path +TransitionServlet.getActionURL(conn,provider,target)) %>" method="post">
<% if( crsf != null ){ %>
<input type='hidden' name='<%=TransitionServlet.TRANSITION_CSRF_ATTR %>' value='<%=crsf %>'/>
<%} %>
<input type='hidden' name='from_view' value='true'/> 
<div class="action_buttons">
<%
for(Object key : provider.getTransitions(target)){

    HtmlBuilder builder = new HtmlBuilder();
    builder.clean(provider.getText(key));
    String valueString = builder.toString();

	if( provider.showTransition(conn,target,key) ){
		String help=provider.getHelp(key);
		if( help == null ){
	 	  %>
	 	  <button class='input_button' type='submit' name='<%=TransitionServlet.TRANSITION_KEY_ATTR %>' value='<%=key.toString() %>'><%=valueString%></button>
	 	  <% 
		}else{
			%>
			<button class='input_button' type='submit' name='<%=TransitionServlet.TRANSITION_KEY_ATTR %>' title='<%=help %>' value='<%=key.toString() %>' ><%=valueString%></button>
			<% 		
		}
	}else{
		if( provider instanceof ShowDisabledTransitions){
			if( ((ShowDisabledTransitions)provider).showDisabledTransition(conn,target,key) ){
				String help=provider.getHelp(key);
				if( help == null ){
			 	  %>
			 	  <button class='input_button disabled' disabled name='<%=TransitionServlet.TRANSITION_KEY_ATTR %>'  value='<%=key.toString() %>' ><%=valueString%></button>
			 	  <% 
				}else{
					%>
					<button class='input_button disabled' disabled name='<%=TransitionServlet.TRANSITION_KEY_ATTR %>' title='<%=help %> (disabled)'  value='<%=key.toString() %>' ><%=valueString%></button>
					<% 		
				}
			}
		}
	}
}
}catch(Exception e){
	conn.error(e,"Error building view target form");
	//throw e;
}
%>
</div>
</form>
</div>
<%= provider.getBottomContent(new HtmlBuilder(),target,session_service).toString() %>
<%@ include file="../std_footer.jsf" %>