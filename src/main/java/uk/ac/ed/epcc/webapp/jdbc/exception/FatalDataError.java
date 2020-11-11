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
 * a fatal error (usually due to a non functioning database)
 * It therefore extends Error and should not be caught other than at the highext level
 * 
 * @author spb
 *
 */


public class FatalDataError extends Error {

	
	public FatalDataError(String message) {
		super(message);
		
	}

	public FatalDataError(Throwable cause) {
		super(cause);
		
	}

	public FatalDataError(String message, Throwable cause) {
		super(message, cause);
		
	}

}