// Copyright - The University of Edinburgh 2013
package uk.ac.ed.epcc.webapp.logging.buffer;

import java.io.PrintWriter;
import java.io.StringWriter;

import uk.ac.ed.epcc.webapp.logging.Logger;

/**
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: BufferLogger.java,v 1.3 2014/09/15 14:30:27 spb Exp $")
public class BufferLogger implements Logger {
    private final StringBuffer buffer;
    private Logger nested;
	/**
	 * 
	 */
	public BufferLogger(StringBuffer buffer, Logger nested) {
		this.buffer=buffer;
		this.nested=nested;
	}

	private void doLog(Object message,Throwable t){
		if(message != null ){
			buffer.append(message);
			buffer.append("\n");
		}
		if( t != null ){
			buffer.append(t.getMessage());
			buffer.append("\n");
			StringWriter writer = new StringWriter();
			t.printStackTrace(new PrintWriter(writer));
			buffer.append(writer.toString());
		}
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.logging.Logger#debug(java.lang.Object)
	 */
	public void debug(Object message) {
		nested.debug(message);
		doLog(message, null);

	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.logging.Logger#debug(java.lang.Object, java.lang.Throwable)
	 */
	public void debug(Object message, Throwable t) {
		nested.debug(message,t);
		doLog(message, t);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.logging.Logger#error(java.lang.Object)
	 */
	public void error(Object message) {
		nested.error(message);
		doLog(message, null);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.logging.Logger#error(java.lang.Object, java.lang.Throwable)
	 */
	public void error(Object message, Throwable t) {
		nested.error(message,t);
		doLog(message, t);

	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.logging.Logger#fatal(java.lang.Object)
	 */
	public void fatal(Object message) {
		nested.fatal(message);
		doLog(message, null);

	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.logging.Logger#fatal(java.lang.Object, java.lang.Throwable)
	 */
	public void fatal(Object message, Throwable t) {
		nested.fatal(message, t);
		doLog(message, t);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.logging.Logger#info(java.lang.Object)
	 */
	public void info(Object message) {
		nested.info(message);
		doLog(message, null);

	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.logging.Logger#info(java.lang.Object, java.lang.Throwable)
	 */
	public void info(Object message, Throwable t) {
		nested.info(message, t);
		doLog(message, t);

	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.logging.Logger#warn(java.lang.Object)
	 */
	public void warn(Object message) {
		nested.warn(message);
		doLog(message, null);

	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.logging.Logger#warn(java.lang.Object, java.lang.Throwable)
	 */
	public void warn(Object message, Throwable t) {
		nested.warn(message, t);
		doLog(message, t);
	}

}
