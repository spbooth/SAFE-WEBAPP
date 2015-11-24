// Copyright - The University of Edinburgh 2014
package uk.ac.ed.epcc.webapp.charts;

/**
	 * @author spb
	 * 
	 */
@uk.ac.ed.epcc.webapp.Version("$Id: InvalidTransformException.java,v 1.3 2014/09/15 14:30:12 spb Exp $")

	public class InvalidTransformException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * @param message
		 */
		public InvalidTransformException(String message) {
			super(message);
		}

	}