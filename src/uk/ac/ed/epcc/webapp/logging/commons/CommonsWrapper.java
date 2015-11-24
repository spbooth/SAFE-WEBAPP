// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.logging.commons;

import org.apache.commons.logging.Log;

import uk.ac.ed.epcc.webapp.logging.Logger;
@uk.ac.ed.epcc.webapp.Version("$Id: CommonsWrapper.java,v 1.6 2014/09/15 14:30:27 spb Exp $")


public class CommonsWrapper implements Logger {
    private final Log log;
    CommonsWrapper(Log log){
    	this.log=log;
    }
	public void debug(Object message) {
		log.debug(message);
	}

	public void debug(Object message, Throwable t) {
		log.debug(message,t);

	}

	public void error(Object message) {
		log.error(message);

	}

	public void error(Object message, Throwable t) {
		log.error(message,t);
	}

	public void fatal(Object message) {
		log.fatal(message);

	}

	public void fatal(Object message, Throwable t) {
		log.fatal(message,t);
	}

	public void info(Object message) {
		log.info(message);

	}

	public void info(Object message, Throwable t) {
		log.info(message,t);

	}

	public void warn(Object message) {
		log.warn(message);

	}

	public void warn(Object message, Throwable t) {
		log.warn(message,t);

	}

}