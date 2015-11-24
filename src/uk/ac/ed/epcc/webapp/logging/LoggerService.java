// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.logging;

import uk.ac.ed.epcc.webapp.AppContextService;

/** A service to provide logging capabilities.
 *<code>
 <pre>
   uk.ac.ed.epcc.webapp.Logger log = c.getService(LoggerService.class).getLogger(getClass()); // gets logger for this class
   log.debug("A debug message");
   log.error("A error message");
</pre>
</code>
 *  Logger is a locally defined interface. In a servlet context this is usually a wrapper round a
 *  Log4J Logger class. However in a stand-alone context a the Logger just prints to <code>System.out</code>
 *  This is to allow lightweight applications to run without the full Log4J jar files and 
 *  makes it easier to switch to a different logging system. 
 * 
 * @author spb
 *
 */
public interface LoggerService extends AppContextService<LoggerService>{
   public Logger getLogger(String name);
   public Logger getLogger(Class c);
}