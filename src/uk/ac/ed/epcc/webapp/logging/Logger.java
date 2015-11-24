// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.logging;

/**
 * This is the Logger interface used by the Webapp classes. Its essentially a
 * wrapper around the subset of the log4J interface that we use but by wrapping
 * all the logging we can reduce dependencies to external packages.
 * 
 * The LoggerService policy object returns the appropriate Logger
 * classes.
 * 
 * @author spb
 * 
 */
public interface Logger {
	public void debug(Object message);

	public void debug(Object message, Throwable t);

	public void error(Object message);

	public void error(Object message, Throwable t);

	public void fatal(Object message);

	public void fatal(Object message, Throwable t);

	public void info(Object message);

	public void info(Object message, Throwable t);

	public void warn(Object message);

	public void warn(Object message, Throwable t);
}