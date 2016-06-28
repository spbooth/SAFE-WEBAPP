//| Copyright - The University of Edinburgh 2011                            |
//|                                                                         |
//| Licensed under the Apache License, Version 2.0 (the "License");         |
//| you may not use this file except in compliance with the License.        |
//| You may obtain a copy of the License at                                 |
//|                                                                         |
//|    http://www.apache.org/licenses/LICENSE-2.0                           |
//|                                                                         |
//| Unless required by applicable law or agreed to in writing, software     |
//| distributed under the License is distributed on an "AS IS" BASIS,       |
//| WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.|
//| See the License for the specific language governing permissions and     |
//| limitations under the License.                                          |
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data.stream;

import java.io.IOException;
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
	public void read(InputStream in) throws DataFault, IOException;

	/**
	 * write contents to an OutputStrean
	 * 
	 * @param out
	 *            OutputStream
	 * @throws DataFault
	 */
	public void write(OutputStream out) throws DataFault, IOException;

}