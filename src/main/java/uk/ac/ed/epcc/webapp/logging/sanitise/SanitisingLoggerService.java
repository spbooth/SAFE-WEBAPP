package uk.ac.ed.epcc.webapp.logging.sanitise;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;

public class SanitisingLoggerService implements LoggerService {

	private LoggerService nested;
	public SanitisingLoggerService(AppContext conn) {
		nested=conn.getService(LoggerService.class);
		if( nested == this) {
			nested=null;
		}
		while( nested != null && nested instanceof SanitisingLoggerService){
    		nested = ((SanitisingLoggerService)nested).nested;
    	}
	}

	@Override
	public Class<? super LoggerService> getType() {
		return LoggerService.class;
	}

	@Override
	public void cleanup() {
		if( nested != null) {
			nested.cleanup();
		}

	}

	@Override
	public Logger getLogger(String name) {
		return new SanitisingLogger(nested.getLogger(name));
	}

	@Override
	public Logger getLogger(Class c) {
		return new SanitisingLogger(nested.getLogger(c));
	}

}
