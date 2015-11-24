// Copyright - The University of Edinburgh 2013
package uk.ac.ed.epcc.webapp.logging.buffer;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;

/**
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: BufferLoggerService.java,v 1.2 2014/09/15 14:30:27 spb Exp $")
public class BufferLoggerService implements LoggerService, Contexed{
    private final AppContext conn;
    private final LoggerService nested;
    private final StringBuffer buffer;
	/**
	 * 
	 */
	public BufferLoggerService(AppContext c) {
		this.conn=c;
		nested = c.getService(LoggerService.class);
		buffer=new StringBuffer();
	}
/* (non-Javadoc)
 * @see uk.ac.ed.epcc.webapp.AppContextService#cleanup()
 */
public void cleanup() {
	nested.cleanup();
}

/* (non-Javadoc)
 * @see uk.ac.ed.epcc.webapp.AppContextService#getType()
 */
public Class<? super LoggerService> getType() {
	return LoggerService.class;
}

/* (non-Javadoc)
 * @see uk.ac.ed.epcc.webapp.logging.LoggerService#getLogger(java.lang.String)
 */
public Logger getLogger(String name) {
	return new BufferLogger(buffer, nested.getLogger(name));
}

/* (non-Javadoc)
 * @see uk.ac.ed.epcc.webapp.logging.LoggerService#getLogger(java.lang.Class)
 */
public Logger getLogger(Class c) {
	return new BufferLogger(buffer, nested.getLogger(c));
}
/* (non-Javadoc)
 * @see uk.ac.ed.epcc.webapp.Contexed#getContext()
 */
	public AppContext getContext() {
		return conn;
	}
	
	public StringBuffer getBuffer(){
		return buffer;
	}
}
	

