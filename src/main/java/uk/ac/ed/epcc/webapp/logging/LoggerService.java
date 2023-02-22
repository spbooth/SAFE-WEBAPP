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
package uk.ac.ed.epcc.webapp.logging;

import java.util.HashMap;
import java.util.Map;

import uk.ac.ed.epcc.webapp.AppContextService;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** A service to provide logging capabilities.
 *<pre>
 <code>
   uk.ac.ed.epcc.webapp.Logger log = c.getService(LoggerService.class).getLogger(getClass()); // gets logger for this class
   log.debug("A debug message");
   log.error("A error message");
</code>
</pre>
 *  Logger is a locally defined interface. In a servlet context this is usually a wrapper round a
 *  Log4J Logger class. However in a stand-alone context a the Logger just prints to <code>System.out</code>
 *  This is to allow lightweight applications to run without the full Log4J jar files and 
 *  makes it easier to switch to a different logging system. 
 * 
 * @author spb
 *
 */
public interface LoggerService extends AppContextService<LoggerService>{
   public static final String SECURITY_LOG = "uk.ac.ed.epcc.webapp.security";
public Logger getLogger(String name);
   public Logger getLogger(Class c);
   
   /** Initialise the logging system. Called once per application
    * 
    */
   public default void initialiseLogging() {
	   
   }
   /** shutdown the logging system called once per application
    * 
    */
   public default void shutdownLogging() {
	   
   }
   /** Log a security event.
    * 
    * 
    * To aid analysis the event string should be a fixed string 
    * for the type of event.
    * Optionally a map of additional attributes can be passed to place the event in context.
    * Attributes from the session such as the current user are generated automatically
    * 
    * 
    * @param event
    * @param sess {@link SessionService}
    * @param context
    * @return
    */
   public default void securityEvent(String event,SessionService sess, Map context) {
	   Logger logger = getLogger(SECURITY_LOG);
	   Map attr = new HashMap();
	   if( context != null) {
		   attr.putAll(context);
	   }
	  if( sess != null ) {
		  sess.addSecurityContext(attr);
	  }
	   if( attr.isEmpty()) {
		   logger.info(event);
	   }else {
		   logger.info(event+": "+attr.toString());
	   }
	   
   }
   public default void securityEvent(String event,SessionService sess) {
	   securityEvent(event,sess,null);
   }
}