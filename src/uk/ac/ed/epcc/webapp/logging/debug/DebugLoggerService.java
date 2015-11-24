// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.logging.debug;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.PreRequisiteService;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;


@PreRequisiteService({LoggerService.class})
@uk.ac.ed.epcc.webapp.Version("$Id: DebugLoggerService.java,v 1.6 2014/09/15 14:30:27 spb Exp $")
/** A {@link LoggerService} where a {@link FatalError} is thrown after logging an error.
 * 
 * @author spb
 *
 */
public class DebugLoggerService implements Contexed, LoggerService {
    private final AppContext conn;
    private LoggerService nested;
    public DebugLoggerService(AppContext conn){
    	this.conn=conn;
    	nested=conn.getService(LoggerService.class);
    }
	
    public LoggerService getNested(){
    	return nested;
    }
	public Logger getLogger(String name) {
		Logger l = null;
		if( nested != null ){
			l = nested.getLogger(name);
		}
		return new DebugLogger(l);
	}


	public Logger getLogger(Class c) {
		Logger l = null;
		if( nested != null ){
			l = nested.getLogger(c);
		}
		return new DebugLogger(l);
	}

	
	public void cleanup() {
		nested.cleanup();
	}
	
	public AppContext getContext() {
		return conn;
	}
	
	

	public Class<? super LoggerService> getType() {
		return LoggerService.class;
	}
}