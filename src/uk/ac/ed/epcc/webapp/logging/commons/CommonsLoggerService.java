// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.logging.commons;

import org.apache.commons.logging.LogFactory;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
@uk.ac.ed.epcc.webapp.Version("$Id: CommonsLoggerService.java,v 1.12 2014/09/15 14:30:27 spb Exp $")


public class CommonsLoggerService implements LoggerService, Contexed {
    LogFactory fac;
    private final AppContext c;
    public CommonsLoggerService(AppContext c){
    	fac = LogFactory.getFactory();
    	this.c=c;
    }
	public Logger getLogger(String name) {
		return new CommonsWrapper(fac.getInstance(name));
	}

	public Logger getLogger(Class c) {
		return new CommonsWrapper(fac.getInstance(c));
	}
	public void cleanup() {
		
	}
	public Class<? super LoggerService> getType() {
		return LoggerService.class;
	}
	public AppContext getContext() {
		return c;
	}
	


}