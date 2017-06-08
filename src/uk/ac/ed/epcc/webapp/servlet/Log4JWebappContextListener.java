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

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.logging.log4j.Log4JLoggerService;

/** ContextListener that ensures Log4J is is correctly configured on application start 
 * and shutdown on application finish.
 * 
 * failing to shutdown or delaying initialisation seems to cause classloader 
 * memory leaks.
 * 
 * @author spb
 *
 */

@WebListener()
public class Log4JWebappContextListener extends WebappContextListener {
    boolean use_log4j=false;
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		Logger log = Logger.getLogger(getClass());
		log.debug("super shutdown");
		super.contextDestroyed(arg0);
		/** The logging scheme may have a similar problem
		 * 
		 */
		if( use_log4j){
			log.debug("Shutting down logging");
		    LogManager.shutdown();
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.servlet.WebappContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		try{
		AppContext conn = ErrorFilter.makeContext(arg0.getServletContext(), null, null);
	
		LoggerService serv = conn.getService(LoggerService.class);
		use_log4j = serv.getClass() == Log4JLoggerService.class;
		serv.getLogger(getClass()).debug("Context started");
		conn.close();
		}catch(Throwable t){
			arg0.getServletContext().log("Error starting Log4JWebappContextListener",t);
		}
	}


}