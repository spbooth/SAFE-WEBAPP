// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.logging.print;

import java.io.PrintStream;

import uk.ac.ed.epcc.webapp.logging.Logger;

/**
 * Implementation of the Logger Interface using simple prints to System.out
 * Allows us to produce lightweight command line applications without the need
 * to include a full Logging framework.
 * 
 * @author spb
 * 
 */
@uk.ac.ed.epcc.webapp.Version("$Id: PrintWrapper.java,v 1.6 2014/09/15 14:30:27 spb Exp $")

public class PrintWrapper implements Logger {
    private final PrintStream dest;
	
    public PrintWrapper(PrintStream dest){
    	this.dest=dest;
    }
    public PrintWrapper(){
    	this(System.out);
    }
	public void debug(Object message) {
		dest.println(message);
	}

	public void debug(Object message, Throwable t) {
		dest.println(message);
		t.printStackTrace(System.err);
	}

	public void error(Object message) {
		dest.println(message);

	}

	public void error(Object message, Throwable t) {
		dest.println(message);
		t.printStackTrace(System.err);

	}

	public void fatal(Object message) {
		dest.println(message);

	}

	public void fatal(Object message, Throwable t) {
		dest.println(message);
		t.printStackTrace(System.err);

	}

	public void info(Object message) {
		dest.println(message);
	}

	public void info(Object message, Throwable t) {
		dest.println(message);
		t.printStackTrace(System.err);

	}

	public void warn(Object message) {
		dest.println(message);

	}

	public void warn(Object message, Throwable t) {
		dest.println(message);
		t.printStackTrace(System.err);

	}

}