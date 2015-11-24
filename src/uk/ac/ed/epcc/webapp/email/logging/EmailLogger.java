// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.email.logging;

import uk.ac.ed.epcc.webapp.logging.Logger;
@uk.ac.ed.epcc.webapp.Version("$Id: EmailLogger.java,v 1.2 2014/09/15 14:30:16 spb Exp $")


public class EmailLogger implements Logger {
    private final Logger nested;
  
    private final EmailLoggerService serv;
    public EmailLogger(EmailLoggerService serv, Logger l){
    	this.serv=serv;
    	this.nested=l;
    }
    /** Send an email error report
     * 
     * @param message
     * @param t
     */
    public void email(Object message, Throwable t){
    	serv.emailError(t, message.toString());
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
		email(message,null);
	}

	
	public void error(Object message, Throwable t) {
		if( nested != null)
		nested.error(message,t);
		email(message,t);
	}


	public void fatal(Object message) {
		if( nested != null)
		nested.fatal(message);
		email(message, null);
	}

	
	public void fatal(Object message, Throwable t) {
		if( nested != null)
		nested.fatal(message, t);
		email(message,t);
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