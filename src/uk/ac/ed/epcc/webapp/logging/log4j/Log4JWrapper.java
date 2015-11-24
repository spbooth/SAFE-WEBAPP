// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.logging.log4j;

import org.apache.log4j.Logger;

import uk.ac.ed.epcc.webapp.Version;

/**
 * Implementation of out Logger interface using Log4J
 * 
 * @author spb
 * 
 */
@uk.ac.ed.epcc.webapp.Version("$Id: Log4JWrapper.java,v 1.10 2014/09/15 14:30:27 spb Exp $")

public class Log4JWrapper implements uk.ac.ed.epcc.webapp.logging.Logger {
	private Logger log;
	private Class<?> target;
	private boolean tagged=false;

	public Log4JWrapper(String name){
		log=Logger.getLogger(name);
	}
	private void tag(){
		if( log != null && target != null && tagged == false){
			tagged = true;
			if(target.isAnnotationPresent(Version.class)){
				Version v=target.getAnnotation(Version.class);
				log.debug(target.getCanonicalName()+" "+v.value());
			}
		}
	}
	public Log4JWrapper(Class<?> c) {
		target=c;
		log = Logger.getLogger(c);
	}

	public void debug(Object message) {
		tag();
		if( log != null ) log.debug(message);
	}

	public void debug(Object message, Throwable t) {
		tag();
		if( log != null )log.debug(message,t);
	}

	public void error(Object message) {
		tag();
		if( log != null )log.error(message);
	}

	public void error(Object message, Throwable t) {
		tag();
		if( log != null )log.error(message, t);
	}

	public void fatal(Object message) {
		tag();
		if( log != null )log.fatal(message);
	}

	public void fatal(Object message, Throwable t) {
		tag();
		if( log != null )log.fatal(message, t);
	}

	public void info(Object message) {
		tag();
		if( log != null )log.info(message);
	}

	public void info(Object message, Throwable t) {
		tag();
		if( log != null )log.info(message, t);
	}

	public void warn(Object message) {
		tag();
		if( log != null )log.warn(message);
	}

	public void warn(Object message, Throwable t) {
		tag();
		if( log != null )log.warn(message, t);
	}
}