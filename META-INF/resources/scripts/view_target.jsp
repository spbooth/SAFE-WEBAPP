<%-- A generic page to view targets of a ViewTransitionProvider
This should always be invoked as a forward from the TransitionServlet.

Note that as the target and provider are encoded in the servlet-path
the form could just submit to self. 
--%>
<%@ page import="uk.ac.ed.epcc.webapp.content.*,uk.ac.ed.epcc.webapp.forms.html.*,uk.ac.ed.epcc.webapp.forms.*, uk.ac.ed.epcc.webapp.forms.transition.*" %>
<%@ page %>
<%@ include file="/session.jsf" %>
<% extra_css="service_desk.css"; %>
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
<%@ include file="/std_header.jsf" %>
<%@ include file="/main__logged_in.jsf" %>
<%= provider.getTopContent(new HtmlBuilder(),target,session_service).toString() %>
<div class="block">
<%= provider.getLogContent(new HtmlBuilder(),target,session_service).toString() %>
<A name="form"/>
<form action="<%=response.encodeURL(web_path +TransitionServlet.getURL(conn,provider,target)+"#form") %>" method="post">
<div class="action_buttons">
<%
for(Object key : provider.getTransitions(target)){

        
    String valueString = provider.getText(key);

	if( provider.allowTransition(conn,target,key) ){
		String help=provider.getHelp(key);
		if( help == null ){
	 	  %>
	 	  <button type='submit' name='<%=TransitionServlet.TRANSITION_KEY_ATTR %>' value='<%=key.toString() %>'><%=valueString%></button>
	 	  <% 
		}else{
			%>
			<button type='submit' name='<%=TransitionServlet.TRANSITION_KEY_ATTR %>' title='<%=help %>' value='<%=key.toString() %>' ><%=valueString%></button>
			<% 		
		}
	}else{
		if( provider instanceof ShowDisabledTransitions){
			if( ((ShowDisabledTransitions)provider).showDisabledTransition(conn,target,key) ){
				String help=provider.getHelp(key);
				if( help == null ){
			 	  %>
			 	  <button disabled name='<%=TransitionServlet.TRANSITION_KEY_ATTR %>'  value='<%=key.toString() %>' ><%=valueString%></button>
			 	  <% 
				}else{
					%>
					<button disabled name='<%=TransitionServlet.TRANSITION_KEY_ATTR %>' title='<%=help %>'  value='<%=key.toString() %>' ><%=valueString%></button>
					<% 		
				}
			}
		}
	}
}
}catch(Throwable e){
	conn.error(e,"Error building view target form");
	//throw e;
}
%>
</div>
</form>
</div>

<%@ include file="/std_footer.jsf" %>
