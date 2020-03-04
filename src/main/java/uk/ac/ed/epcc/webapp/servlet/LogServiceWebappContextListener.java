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
package uk.ac.ed.epcc.webapp.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.logging.LoggerService;

/** ContextListener that ensures the {@link LoggerService} is is correctly configured on 
 * application start 
 * and shutdown on application finish.
 * 
 * failing to shutdown or delaying initialisation seems to cause classloader 
 * memory leaks.
 * 
 * @author spb
 *
 */

@WebListener()
public class LogServiceWebappContextListener extends WebappContextListener {
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		AppContext conn=null;
		try{
			conn = ErrorFilter.makeContext(arg0.getServletContext(), null, null);

			LoggerService serv = conn.getService(LoggerService.class);
			serv.shutdownLogging();


		}catch(Exception t){
			arg0.getServletContext().log("Error starting logging WebappContextListener",t);
		}finally {
			if( conn != null) {
				conn.close();
			}
		}
		super.contextDestroyed(arg0);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.servlet.WebappContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		AppContext conn=null;
		try{
			conn = ErrorFilter.makeContext(arg0.getServletContext(), null, null);

			LoggerService serv = conn.getService(LoggerService.class);
			serv.initialiseLogging();
			serv.getLogger(getClass()).info("Logging initialised");


		}catch(Exception t){
			arg0.getServletContext().log("Error starting logging WebappContextListener",t);
		}finally {
			if( conn != null) {
				conn.close();
			}
		}
	}


}