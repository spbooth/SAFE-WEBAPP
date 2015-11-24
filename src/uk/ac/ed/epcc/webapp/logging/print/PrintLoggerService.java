// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.logging.print;

import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
@uk.ac.ed.epcc.webapp.Version("$Id: PrintLoggerService.java,v 1.11 2014/09/15 14:30:27 spb Exp $")


public class PrintLoggerService implements LoggerService {

	public Logger getLogger(String name) {
		return new PrintWrapper();
	}

	public Logger getLogger(Class c) {
		return new PrintWrapper();
	}

	public void cleanup() {
		
	}

	public Class<? super LoggerService> getType() {
		return LoggerService.class;
	}

	

}