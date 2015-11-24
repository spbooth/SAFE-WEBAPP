<%--
Generic page to implement form transitions

This should allways be forwarded to from the TransitionServlet where parameters are passed by
attribute.

Note that as the target and provider are encoded in the servlet-path
the form could just submit to self. This might break form error reporting though.
--%>
<%@ page import="uk.ac.ed.epcc.webapp.forms.html.*" %>
<%@ page import="uk.ac.ed.epcc.webapp.forms.*" %>
<%@ page import="uk.ac.ed.epcc.webapp.forms.transition.*" %>
<%@ page import="uk.ac.ed.epcc.webapp.forms.exceptions.*" %>
<%@ page import="uk.ac.ed.epcc.webapp.forms.inputs.*" %>
<%@ page import="uk.ac.ed.epcc.webapp.content.*" %>
<%@ include file="/session.jsf" %>
<% extra_css="service_desk.css"; %>
<%
    TransitionFactory tp = TransitionServlet.getProvider(conn,request);
    Object key =  request.getAttribute(TransitionServlet.TRANSITION_KEY_ATTR);
    // note key will always be null if page invoked directly.
    // As actions are performed by the servlet that does access control the only
    // risk is information leakage.
    if( tp == null || key == null ){
%>
<jsp:forward page="/messages.jsp?message_type=invalid_input" />
<%
       return;
    }
    Object target =   TransitionServlet.getTarget(conn,tp,request);
    String action = key.toString();
    String page_title="Transition";
    String page_heading="Transition";
    if( tp instanceof TitleTransitionFactory){
    	TitleTransitionFactory ttp = (TitleTransitionFactory)tp;
    	page_title=ttp.getTitle(key, target);
    	page_heading=ttp.getHeading(key, target);
    }else{
    	String type=tp.getTargetName();
    	String type_title = conn.getInitParameter("transition_title."+type,type);
    	if( type==null) type="";
		page_title = service_name+" "+action+" "+type_title;
		page_heading = action+" "+type_title;
    }
    // Add per tranistion css for script augmented transitions.
    String param_name="transition_css."+action;
    String transition_css = conn.getInitParameter(param_name);
    if( transition_css != null && transition_css.trim().length() > 0){
    	extra_css= extra_css+","+transition_css;
    }
%>
<%@ include file="/std_header.jsf" %>
<%@ include file="/main__logged_in.jsf" %>
<% if( tp instanceof NavigationProvider){
   HtmlBuilder top = new HtmlBuilder();
   ((NavigationProvider)tp).getTopNavigation(top, target);
%><%=top.toString() %><%
}
%>
<%
HTMLForm f = new HTMLForm(conn);
Transition t = tp.getTransition(target,key);
try{
if( t instanceof BaseFormTransition ){
	BaseFormTransition ft = (BaseFormTransition) t;
	ft.buildForm(f,target,conn);
}else if( t instanceof TargetLessTransition ){
	TargetLessTransition tlt = (TargetLessTransition) t;
	((TargetLessTransition)t).buildForm(f,conn);
}

boolean multi = f.containsInput(FileInput.class);
HtmlBuilder form_content = new HtmlBuilder();
// Don't use period to be jquery compatible
form_content.setFormID("transition_");
if( ! HTMLForm.hasError(request) && t instanceof ValidatingFormTransition){
	// force initial validation and use internal state
	Collection<String> m = getMissing(request);
	Map<String,String> e = getErrors(request);
	f.validate(m,e);
    form_content.setMissingFields(m);
    form_content.setErrors(e);
	form_content.setPostParams(null);
}else{
	// use post params and cached errors.
	ServletService serv = conn.getService(ServletService.class);
	if( serv != null){
		form_content.setPostParams(serv.getParams());
	}else{
		form_content.setPostParams(new HashMap<String,Object>());
	}
	form_content.setMissingFields(getMissing(request));
	form_content.setErrors(getErrors(request));
}
%>
<%@ include file="/scripts/form_context.jsf" %>
<div class="block">
<h2><%=page_heading %></h2>
<A name="summary"/>
<% if(target != null ){ %>
<%= tp.getSummaryContent(conn,new HtmlBuilder(),target).toString() %>
<% } %>
<%
	if( t instanceof ExtraContent ){
%>
<A name="extra"/>
<%=((ExtraContent) t).getExtraHtml(new HtmlBuilder(),session_service,target).toString()%>
<%} %>
<A name="form"/>
<form method="post" 
<% if( multi ){ %>
   enctype="multipart/form-data"
<% } %>
action="<%= response.encodeURL(web_path+TransitionServlet.getURL(conn,tp,target))%>">
<input type='hidden' name='<%=TransitionServlet.TRANSITION_KEY_ATTR %>' value='<%=action %>'/>
<input type='hidden' name='transition_form' value='true'/>
<input type='hidden' name='form_url' value='/scripts/transition.jsp'/>
<% 
if( t instanceof CustomFormContent ){
	((CustomFormContent)t).addFormContent(form_content, session_service, f, target);
}else{
	form_content.addFormTable(conn, f);
	form_content.addActionButtons(f);
}

%>
<%= form_content.toString() %>
</form>
</div>
<%
}catch(TransitionException e){
%>
	<div class="block">
	<h2>Form Error</h2>
	<p> An error has occurred while creating this form. 
	</p>
	<p>
	<span class="warn"><%=e.getMessage() %></span>
	</p>
	</div>
<%
}
%>
<% if( tp instanceof NavigationProvider){
   HtmlBuilder bottom = new HtmlBuilder();
   ((NavigationProvider)tp).getBottomNavigation(bottom, target);
%><%=bottom.toString() %><%
}
%>
<%@ include file="/std_footer.jsf" %>
