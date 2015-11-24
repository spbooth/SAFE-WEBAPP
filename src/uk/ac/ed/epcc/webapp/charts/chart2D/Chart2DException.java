// Copyright - The University of Edinburgh 2014
package uk.ac.ed.epcc.webapp.charts.chart2D;

/**
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: Chart2DException.java,v 1.3 2014/09/15 14:30:13 spb Exp $")
public class Chart2DException extends Exception {

	/**
	 * 
	 */
	public Chart2DException() {
	}

	/**
	 * @param message
	 */
	public Chart2DException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public Chart2DException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public Chart2DException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public Chart2DException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
