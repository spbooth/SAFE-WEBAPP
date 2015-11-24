// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.logging.log4j;

import org.apache.log4j.PropertyConfigurator;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.config.ConfigService;
import uk.ac.ed.epcc.webapp.config.ConfigServiceListener;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
/** LoggerService that uses Log4J
 * The first time the service is made it will configure loggers using the
 * properties from the ConfigService
 * 
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: Log4JLoggerService.java,v 1.17 2014/09/15 14:30:27 spb Exp $")

public class Log4JLoggerService implements LoggerService, Contexed , ConfigServiceListener{

	private static boolean initialised=false;
	private final AppContext conn;
	public static void reset(){
		initialised=false;
	}
	public Log4JLoggerService(AppContext c){
		this.conn=c;
		ConfigService service = c.getService(ConfigService.class);
		if( service != null ){
			service.addListener(this);
		}
		if( ! initialised ){
			initialised=true;
			
			if( service != null){
				PropertyConfigurator.configure(service.getServiceProperties());
			}
		}
	}
	public Logger getLogger(String name) {
		return new Log4JWrapper(name);
	}

	public Logger getLogger(Class c) {
		return new Log4JWrapper(c);
	}
	public void cleanup() {
		
	}
	public void resetConfig() {
		getLogger(getClass()).debug("Resetting initialised flag");
		initialised=false;
		
	}
	public Class<? super LoggerService> getType() {
		return LoggerService.class;
	}
	public AppContext getContext() {
		return conn;
	}

	
}