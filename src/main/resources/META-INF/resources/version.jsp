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
Display version information held in properties

--%>
<%@page import="uk.ac.ed.epcc.webapp.email.logging.EmailLoggerService"%>
<%@ page
	import="uk.ac.ed.epcc.webapp.*, uk.ac.ed.epcc.webapp.model.*, java.util.* "%>
<%@ include file="/service_init.jsf"%>
<%
    String page_title = service_name+" "+website_name+" Versions";
%>
<%@ include file="/std_header.jsf"%>
<h2>module versions</h2>
<ul>
<%
// show same set as reported in email
Properties props = conn.getService(ConfigService.class).getServiceProperties();
FilteredProperties version = new FilteredProperties(props, EmailLoggerService.VERSION_PROP_PREFIX);
		
for(String name : version.names()){
	String ver = version.getProperty(name);
	if( ver != null ){
%><li><%=name %>: <%=ver %></li><% 
	}
}
%>
</ul>
<%@ include file="/std_footer.jsf"%>