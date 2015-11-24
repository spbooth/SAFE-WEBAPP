// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data.stream;




/**
 * varient of StreamData where the data has an associated mime type and filename
 * Classes that implement this interface can also implement javax.activation.DataSource
 * @author spb
 * 
 */
public interface MimeStreamData extends StreamData {
	/**
	 * get the Mime type associated with the Data
	 * 
	 * @return String Mime type
	
	 */
	public String getContentType();

	/**
	 * Get the original filename associated with the data.
	 * 
	 * @return String filename or null
	
	 */
	public String getName();
}