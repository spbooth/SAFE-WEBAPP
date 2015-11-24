// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.servlet.logging;

import javax.servlet.ServletContext;

import uk.ac.ed.epcc.webapp.logging.Logger;


/** A {@link Logger} that reports to the {@link ServletContext} logging methods.
 * 
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: ServletWrapper.java,v 1.2 2014/09/15 14:30:35 spb Exp $")
public class ServletWrapper implements Logger {
    private final ServletContext ctx;
    private Level level=Level.INFO;
    public enum Level{
    	FATAL,
    	ERROR,
    	INFO,
    	WARN,
    	DEBUG;
    }
    public void setLevel(Level l){
    	level=l;
    }
    public ServletWrapper(ServletContext ctx){
    	this.ctx=ctx;
    }
	public void debug(Object message) {
		if( level.compareTo(Level.DEBUG) >= 0 ){
			ctx.log(message.toString());
		}
		
	}

	public void debug(Object message, Throwable t) {
		if( level.compareTo(Level.DEBUG) >= 0 ){
			ctx.log(message.toString(),t);
		}
	}

	public void error(Object message) {
		if( level.compareTo(Level.ERROR) >= 0 ){
			ctx.log(message.toString());
		}
	}

	public void error(Object message, Throwable t) {
		if( level.compareTo(Level.ERROR) >= 0 ){
			ctx.log(message.toString(),t);
		}
	}

	public void fatal(Object message) {
		if( level.compareTo(Level.FATAL) >= 0 ){
			ctx.log(message.toString());
		}
	}

	public void fatal(Object message, Throwable t) {
		if( level.compareTo(Level.FATAL) >= 0 ){
			ctx.log(message.toString(),t);
		}
	}

	public void info(Object message) {
		if( level.compareTo(Level.INFO) >= 0 ){
			ctx.log(message.toString());
		}
	}

	public void info(Object message, Throwable t) {
		if( level.compareTo(Level.INFO) >= 0 ){
			ctx.log(message.toString(),t);
		}
	}

	public void warn(Object message) {
		if( level.compareTo(Level.WARN) >= 0 ){
			ctx.log(message.toString());
		}
	}

	public void warn(Object message, Throwable t) {
		if( level.compareTo(Level.WARN) >= 0 ){
			ctx.log(message.toString(),t);
		}
	}

}