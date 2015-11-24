// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data.stream;

import java.io.InputStream;
import java.io.OutputStream;

import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;

/**
 * Interface implemented by Objects that can provide their data as a Stream
 * 
 * @author spb
 * 
 */
public interface StreamData {
	/**
	 * get the size of data held in the object
	 * 
	 * @return long size of current data
	 */
	public long getLength() ;

	/**
	 * get an InputStream to read the state of the obect
	 * 
	 * @return InputStream
	 */
	public InputStream getInputStream() ;

	/**
	 * Get an OutputStream used to modify the state of the object
	 * 
	 * @return OutPutStream
	 */
	public OutputStream getOutputStream() ;

	/**
	 * read data from an Input Stream
	 * 
	 * @param in
	 *            InputStream
	 * @throws DataFault
	 */
	public void read(InputStream in) throws DataFault;

	/**
	 * write contents to an OutputStrean
	 * 
	 * @param out
	 *            OutputStream
	 * @throws DataFault
	 */
	public void write(OutputStream out) throws DataFault;

}