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