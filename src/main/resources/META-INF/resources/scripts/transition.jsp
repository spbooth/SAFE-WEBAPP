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
Generic page to implement form transitions

This should allways be forwarded to from the TransitionServlet where parameters are passed by
attribute.

Note that as the target and provider are encoded in the servlet-path
the form could just submit to self. This might break form error reporting though.
--%>
<%@page import="java.util.*" %>
<%@page import="uk.ac.ed.epcc.webapp.forms.result.ChainedTransitionResult"%>
<%@page import="uk.ac.ed.epcc.webapp.tags.WebappHeadTag"%>
<%@ page import="uk.ac.ed.epcc.webapp.forms.html.*" %>
<%@ page import="uk.ac.ed.epcc.webapp.forms.*" %>
<%@ page import="uk.ac.ed.epcc.webapp.forms.transition.*" %>
<%@ page import="uk.ac.ed.epcc.webapp.forms.exceptions.*" %>
<%@ page import="uk.ac.ed.epcc.webapp.forms.inputs.*" %>
<%@ page import="uk.ac.ed.epcc.webapp.content.*" %>
<%@ page import="uk.ac.ed.epcc.webapp.session.SessionService" %>
<%@ page import="uk.ac.ed.epcc.webapp.servlet.ServletService" %>
<%@ page import="uk.ac.ed.epcc.webapp.servlet.TransitionServlet" %>
<%@ taglib uri="http://safe.epcc.ed.ac.uk/webapp" prefix="wb" %>
<wb:ServiceInit/>
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
    boolean allow_anonymous = tp instanceof AnonymousTransitionFactory;
    if( ! allow_anonymous ){
%>
<wb:session/>
<%    	
    }
    Object target =   TransitionServlet.getTarget(conn,tp,request);
    String action = key.toString();
    String page_title=TransitionServlet.getPageTitle(tp, key, target);
    String page_heading=TransitionServlet.getPageHeader(tp, key, target);
    String crsf = TransitionServlet.getCrsfToken(conn, request);
    SessionService session_service = conn.getService(SessionService.class);
    // Add per tranistion css for script augmented transitions.
   
    if( tp instanceof ScriptTransitionFactory){
    	ScriptTransitionFactory st = (ScriptTransitionFactory)tp;
    	WebappHeadTag.addScript(conn,request,st.getAdditionalScript(key));
    	WebappHeadTag.addCss(conn, request, st.getAdditionalCSS(key));
    }
    request.setAttribute(WebappHeadTag.FORM_PAGE_ATTR, Boolean.TRUE);
%>
<%@ taglib uri="http://safe.epcc.ed.ac.uk/webapp" prefix="wb" %>
<wb:formpage/>
<wb:css url="service_desk.css"/>
<%@ include file="../std_header.jsf" %>
<%@ include file="../main__logged_in.jsf" %>
<%@ include file="../back.jsf" %>
<% if( tp instanceof NavigationProvider){
   HtmlBuilder top = new HtmlBuilder();
   ((NavigationProvider)tp).getTopNavigation(top, target);
%><%=top.toString() %><%
}
%>
<%
Transition t = tp.getTransition(target,key);
HTMLForm f = new HTMLForm(conn,new ChainedTransitionResult(tp,target,key));
//Don't use period to be jquery compatible
f.setFormID("transition.");
try{
if( t instanceof BaseFormTransition ){
	BaseFormTransition ft = (BaseFormTransition) t;
	ft.buildForm(f,target,conn);
}else if( t instanceof TargetLessTransition ){
	TargetLessTransition tlt = (TargetLessTransition) t;
	((TargetLessTransition)t).buildForm(f,conn);
}

String default_charset = conn.getService(ServletService.class).defaultCharset();
boolean multi = f.containsInput(FileInput.class);
HtmlBuilder form_builder = new HtmlBuilder();
HtmlFormPolicy form_content=form_builder.getFormPolicy();
if( ! HTMLForm.hasError(request) && t instanceof ValidatingFormTransition){
	// force initial validation and use internal state
	Collection<String> m = HTMLForm.getMissing(request);
	Map<String,String> e = HTMLForm.getErrors(request);
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
	form_content.setMissingFields(HTMLForm.getMissing(request));
	form_content.setErrors(HTMLForm.getErrors(request));
}
%>
<div class="block">
<h1><%=new HtmlBuilder().clean(page_heading).toString() %></h1>

<% if(target != null ){ %>
<%= tp.getSummaryContent(conn,new HtmlBuilder(),target).toString() %>
<% } %>
<%
	if( t instanceof ExtraContent ){
		HtmlBuilder extra=(HtmlBuilder)((ExtraContent) t).getExtraHtml(new HtmlBuilder(),session_service,target,f);
		if( extra != null && extra.hasContent()){
%>
<%=extra.toString()%>
<%
		}else{
		  if( extra == null){
		  	conn.error("Null builder from ExtraContent in transition.jsp ");
		  }
    	}
	}
%>
<wb:FormContext inline="true"/>
<form id="form" class="transition" method="post" 
<% if( multi ){ %>
   enctype="multipart/form-data"
<% } %>
<% if( default_charset != null && ! default_charset.isEmpty()){ %> accept-charset="<%=default_charset %>"
<% } %>
action="<%= response.encodeURL(web_path+TransitionServlet.getURL(conn,tp,target))%>" role="main">
<input type='hidden' name='<%=TransitionServlet.TRANSITION_KEY_ATTR %>' value='<%=action %>'/>
<% if( crsf != null ){ %>
<input type='hidden' name='<%=TransitionServlet.TRANSITION_CSRF_ATTR %>' value='<%=crsf %>'/>
<%} %>
<input type='hidden' name='transition_form' value='true'/>
<% if(f.getTargetStage() > 0 ){ %>
<input type='hidden' name='<%=BaseHTMLForm.FORM_STAGE_INPUT %>' value='<%=f.getTargetStage()%>'/>
<%} %>
<% 
form_content.setLockedAsHidden(f.getTargetStage()>0);
if( t instanceof CustomFormContent ){
	((CustomFormContent)t).addFormContent(form_builder, session_service, f, target);
}else{
	form_content.setActionName(f.getActionName());
	form_builder.addFormTable(conn, f);
	form_builder.addActionButtons(f);
}

%>
<%= form_builder.toString() %>
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
	<span class="warn"><%=new HtmlBuilder().clean(e.getMessage()) %></span>
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
<%@ include file="../std_footer.jsf" %>