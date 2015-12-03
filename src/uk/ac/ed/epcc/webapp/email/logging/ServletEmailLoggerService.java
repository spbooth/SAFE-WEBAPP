//| Copyright - The University of Edinburgh 2011                            |
//|                                                                         |
//| Licensed under the Apache License, Version 2.0 (the "License");         |
//| you may not use this file except in compliance with the License.        |
//| You may obtain a copy of the License at                                 |
//|                                                                         |
//|    http://www.apache.org/licenses/LICENSE-2.0                           |
//|                                                                         |
//| Unless required by applicable law or agreed to in writing, software     |
//| distributed under the License is distributed on an "AS IS" BASIS,       |
//| WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.|
//| See the License for the specific language governing permissions and     |
//| limitations under the License.                                          |
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.email.logging;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.AppContextService;
/** An EmailLoggerService that adds information about the servlet request
 * parameters to error emails.
 * 
 * @author spb
 *
 */


public class ServletEmailLoggerService extends EmailLoggerService {
	
    @SuppressWarnings("unchecked")
	@Override
	protected Hashtable getProps() {
		Hashtable props= super.getProps();
		if (req != null) {
			String url = req.getRequestURL().toString();
			if( url != null && url.contains("password")){
				url="redacted";
			}
			if( url != null ){
				props.put("request_url", url);
			}
			
			// Get the user-agent info
			Vector<String> headers = new Vector<String>();
			for (Enumeration enumeration = req.getHeaderNames(); enumeration
			.hasMoreElements();) {
				String header = (String) enumeration.nextElement();
				if( ! header.contains("cookie") && ! header.contains("authorization")){
					// don't log security sensative info
					headers.add("    " + header + " = '" + req.getHeader(header)
							+ "'\n");
				}
			}
			props.put("headers", headers);

			StringBuilder service_list = new StringBuilder();
			for(AppContextService s : getContext().getServices()){
				service_list.append("   ");
				service_list.append(s.getType().getSimpleName());
				service_list.append(": ");
				service_list.append(s.getClass().getCanonicalName());
				service_list.append("\n");
			}
			props.put("services", service_list.toString());
			// Show IP Address of current remote client
			String ip_address = null;

			ip_address = req.getRemoteAddr();

			if (ip_address != null) {
				props.put("ip_address", ip_address);
			}

			// And show all parameters
			StringBuilder psb = new StringBuilder();

			for (Enumeration param_names = req.getParameterNames(); param_names
			.hasMoreElements();) {
				String name = (String) param_names.nextElement();
				if( ! name.equalsIgnoreCase("password")){
					psb.append("  ");
					psb.append(name); 

					String val = req.getParameter(name);
					if( val.length() < 512){
						psb.append(" = ");
						psb.append(val);
					}else{
						psb.append(" - long parameter");
					}
					psb.append("\n");
				}
			}

			if (psb.length() > 0) {
				props.put("parameters", psb.toString());
			}
		}
		return props;
	}
	private final HttpServletRequest req;
	public ServletEmailLoggerService(AppContext conn,HttpServletRequest req) {
		super(conn);
		this.req=req;
	}
	@Override
	protected synchronized void emailError(Throwable e, String text) {
		// get the inner exception for a ServletException
		Throwable t;
		if( e instanceof ServletException){
			t = ((ServletException)e).getRootCause();
			if( t == null ){
				t=e.getCause();
			}
			if( t == null){
				t=e;
			}
		}else{
			t=e;
		}
		super.emailError(t, text);
	}

	
}