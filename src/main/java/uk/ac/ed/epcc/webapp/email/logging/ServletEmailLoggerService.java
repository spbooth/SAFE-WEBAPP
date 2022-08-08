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

import java.util.Hashtable;

import javax.servlet.ServletException;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.servlet.ServletService;
import uk.ac.ed.epcc.webapp.timer.TimerService;
/** An EmailLoggerService that adds information about the servlet request
 * parameters to error emails.
 * 
 * If no {@link ServletService} is installed then it behaves like a normal {@link EmailLoggerService}
 * 
 * @author spb
 *
 */
public class ServletEmailLoggerService extends EmailLoggerService {
	
    @SuppressWarnings("unchecked")
	@Override
	protected Hashtable getProps() {
		Hashtable props= super.getProps();
		try{
			ServletService serv = getContext().getService(ServletService.class);
			if( serv != null) {
				serv.addErrorProps(props);
			}

			TimerService timer = getContext().getService(TimerService.class);
			if( timer != null){
				StringBuilder sb = new StringBuilder();
				timer.timerStats(sb);
				if( sb.length() > 0){
					props.put("timers", sb.toString());
				}
			}
		}catch(Exception t){
			getSelfLogger().error("Error getting servlet props", t);
		}
		return props;
    }
	public ServletEmailLoggerService(AppContext conn) {
		super(conn);
	}
	@Override
	protected synchronized void emailError(LogLevels level,Throwable e, String text) {
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
		super.emailError(level,t, text);
	}

	
}