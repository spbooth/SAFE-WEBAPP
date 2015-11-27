// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.apps;

import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;

/**
 * Logger service for command line tools. It is assumed that command line
 * applications will use standard output to output their result. To avoid
 * polluting their output, this logger writes all logging messages to standard
 * error (even info and debug messages). This has the advantage that
 * applications can have their output piped into another application or file but
 * logging information to the user will still be printed on the command line
 * (unless, of course standard error is redirected)
 * 
 * @author jgreen4
 * 
 */

public class CommandLineLoggerService implements LoggerService, Logger {

	/**
	 * The various levels the debugger can log at
	 */
	public enum Level {
		FATAL, ERROR, INFO, WARN, DEBUG;
	}

	/**
	 * Marks whether or not stack traces should be printed. Useful for debugging
	 */
	private boolean printStackTrace = false;
	private boolean printMessage = true;
	/**
	 * The level at which the logger can log
	 */
	private Level level = Level.INFO;

	/**
	 * Constructs a new <code>CommandLineLoggerService</code> that will not print
	 * stack traces when exceptions are presented and with level set to
	 * Level.INFO.
	 * 
	 */
	public CommandLineLoggerService() {
	}

	/**
	 * Constructs a new <code>CommandLineLoggerService</code> whose settings are
	 * based on <code>orig</code>.
	 * 
	 * @param orig
	 *          The <code>CommandLineLoggerService</code> whose settings should be
	 *          used to configure this service
	 */
	private CommandLineLoggerService(CommandLineLoggerService orig) {
		this.printStackTrace = orig.printStackTrace;
		this.printMessage = orig.printMessage;
		this.level = orig.level;
	}

	/**
	 * Sets whether or not this logger prints stack traces when it's logging
	 * messages are passed exceptions
	 * 
	 * @param b
	 *          If <code>true</code> stack traces will be printed. If
	 *          <code>false</code> they won't
	 */
	public void printStackTraces(boolean b) {
		this.printStackTrace = b;
	}
	

	/**
	 * Sets whether or not this logger prints the exception message when it's logging
	 * messages are passed exceptions
	 * 
	 * @param b
	 *          If <code>true</code> message will be printed. If
	 *          <code>false</code> they won't
	 */
	public void printMessages(boolean b) {
		this.printMessage = b;
	}

	/**
	 * Sets the level at which this logger logs
	 * 
	 * @param level
	 *          The level at which this logger logs
	 */
	public void setLevel(Level level) {
		this.level = level;
	}

	/*
	 * ##########################################################################
	 * IMPLEMENTED METHODS REQUIRED BY LoggerService
	 * ##########################################################################
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.epcc.webapp.LoggerService#getLogger(java.lang.String)
	 */
	public Logger getLogger(String name) {
		return new CommandLineLoggerService(this);
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.LoggerService#getLogger(java.lang.Class)
	 */
	public Logger getLogger(Class c) {
		return new CommandLineLoggerService(this);
	}
	
	/*
	 * ##########################################################################
	 * IMPLEMENTED METHODS REQUIRED BY Logger
	 * ##########################################################################
	 */

	/*
	 * (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Logger#debug(java.lang.Object)
	 */
	public void debug(Object message) {
		if (level.compareTo(Level.DEBUG) >= 0) {
			System.err.println(message);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Logger#debug(java.lang.Object, java.lang.Throwable)
	 */
	public void debug(Object message, Throwable t) {
		if (level.compareTo(Level.DEBUG) >= 0) {
			System.err.println(message);
			if(this.printMessage && t != null){
				System.err.println(t.getMessage());
			}
			if (this.printStackTrace && t!= null) {
				t.printStackTrace(System.err);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Logger#error(java.lang.Object)
	 */
	public void error(Object message) {
		if (level.compareTo(Level.ERROR) >= 0) {
			System.err.println(message);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Logger#error(java.lang.Object, java.lang.Throwable)
	 */
	public void error(Object message, Throwable t) {
		if (level.compareTo(Level.ERROR) >= 0) {
			System.err.println(message);
			if(this.printMessage && t != null){
				System.err.println(t.getMessage());
			}
			if (this.printStackTrace && t!= null) {
				t.printStackTrace(System.err);
			}
			
		}
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Logger#fatal(java.lang.Object)
	 */
	public void fatal(Object message) {
		if (level.compareTo(Level.FATAL) >= 0) {
			System.err.println(message);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Logger#fatal(java.lang.Object, java.lang.Throwable)
	 */
	public void fatal(Object message, Throwable t) {
		if (level.compareTo(Level.FATAL) >= 0) {
			System.err.println(message);
			if(this.printMessage && t != null){
				System.err.println(t.getMessage());
			}
			if (this.printStackTrace && t!= null) {
				t.printStackTrace(System.err);
			}
			
		}
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Logger#info(java.lang.Object)
	 */
	public void info(Object message) {
		if (level.compareTo(Level.INFO) >= 0) {
			System.err.println(message);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Logger#info(java.lang.Object, java.lang.Throwable)
	 */
	public void info(Object message, Throwable t) {
		if (level.compareTo(Level.INFO) >= 0) {
			System.err.println(message);
			if(this.printMessage && t != null){
				System.err.println(t.getMessage());
			}
			if (this.printStackTrace && t!= null) {
				t.printStackTrace(System.err);
			}
			
		}
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Logger#warn(java.lang.Object)
	 */
	public void warn(Object message) {
		if (level.compareTo(Level.WARN) >= 0) {
			System.err.println(message);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Logger#warn(java.lang.Object, java.lang.Throwable)
	 */
	public void warn(Object message, Throwable t) {
		if (level.compareTo(Level.WARN) >= 0) {
			System.err.println(message);
			if(this.printMessage && t != null){
				System.err.println(t.getMessage());
			}
			if (this.printStackTrace && t!= null) {
				t.printStackTrace(System.err);
			}
			
		}
	}

	public void cleanup() {
		
		
	}

	public Class<? super LoggerService> getType() {
		return LoggerService.class;
	}


}