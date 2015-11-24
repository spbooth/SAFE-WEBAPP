// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.logging.debug;

import uk.ac.ed.epcc.webapp.logging.Logger;
@uk.ac.ed.epcc.webapp.Version("$Id: DebugLogger.java,v 1.5 2014/09/15 14:30:27 spb Exp $")

/** A {@link Logger} that throws a {@link FatalError} after logging an error.
 * 
 * @author spb
 *
 */
public class DebugLogger implements Logger {
    private final Logger nested;
  
    public DebugLogger(Logger l){
    	this.nested=l;
    }
  
	
	public void debug(Object message) {
		if( nested != null)
		nested.debug(message);
	}

	
	public void debug(Object message, Throwable t) {
		if( nested != null)
		nested.debug(message, t);
	}

	
	public void error(Object message) {
		if( nested != null)
		nested.error(message);
		throw new FatalError(message.toString());
	}

	
	public void error(Object message, Throwable t) {
		if( nested != null)
		nested.error(message,t);
		throw new FatalError(message.toString(),t);
	}


	public void fatal(Object message) {
		if( nested != null)
		nested.fatal(message);
		throw new FatalError(message.toString());
	}

	
	public void fatal(Object message, Throwable t) {
		if( nested != null)
		nested.fatal(message, t);
		throw new FatalError(message.toString(), t);
	}

	
	public void info(Object message) {
		if( nested != null)
		nested.info(message);

	}

	
	public void info(Object message, Throwable t) {
		if( nested != null)
		nested.info(message,t);

	}

	
	public void warn(Object message) {
		if( nested != null)
		nested.warn(message);
	}

	
	public void warn(Object message, Throwable t) {
		if( nested != null)
		nested.warn(message, t);

	}

}