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
package uk.ac.ed.epcc.webapp.jdbc.exception;
/** A Data error similar to a DataException but this is taken to be
 * a normally non recoverable error (hence an uncaught exception)
 * 
 * @author spb
 *
 */


public class DataError extends RuntimeException {

	
	public DataError(String message) {
		super(message);
		
	}

	public DataError(Throwable cause) {
		super(cause);
		
	}

	public DataError(String message, Throwable cause) {
		super(message, cause);
		
	}

}