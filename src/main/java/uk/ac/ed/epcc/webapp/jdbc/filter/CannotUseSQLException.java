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
package uk.ac.ed.epcc.webapp.jdbc.filter;
/** Exception thrown when a requested operation cannot be performed via SQL
 * 
 * @author spb
 *
 */


public class CannotUseSQLException extends Exception {

	public CannotUseSQLException() {
		super();
	}

	public CannotUseSQLException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public CannotUseSQLException(String message) {
		super(message);
		
	}

	public CannotUseSQLException(Throwable cause) {
		super(cause);
		
	}

}