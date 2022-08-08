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
errorpage.jsp - Page used to trap and process servlet exceptions and errors

  Arguments:
	none

  Links:

Note make sure that this page does not actually output any whitepace 
otherwise the redirect will fail

This version creates an AppContext to allow config in the header/footer.
This needs to be explicilty closed as the errorpage runs outside the ErrorFilter
--%>
<%@page isErrorPage="true" %>
<%@page session="false" %>
<%@ taglib uri="http://safe.epcc.ed.ac.uk/webapp" prefix="wb" %>
<wb:ServiceInit/>
<%try{ %>
<% String page_title = "Errorpage"; %>
<%@ include file="std_header.jsf"%>
<%@ include file="scripts/errorpage.jsf" %>
<%@ include file="std_footer.jsf"%>
<%}finally{
	// Errorpage is called outside the filter
	if( conn != null){
		conn.close();
	}
}
%>