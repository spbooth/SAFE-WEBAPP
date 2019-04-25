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
package uk.ac.ed.epcc.webapp.jdbc.expr;

import uk.ac.ed.epcc.webapp.jdbc.filter.NoSQLFilterException;

/** Exception thrown when the requested Filter cannot be generated.
 * 
 * This is a very strong assertion that the expression cannot be generated at all.
 * If a {@link FilterProvider} has insufficient information to generate the filter
 * (for example it is relying on a nested class that does not implement {@link FilterProvider}
 * it should throw {@link NoSQLFilterException} instead.
 * 
 * @see NoSQLFilterException
 * @author spb
 *
 */


public class CannotFilterException extends Exception {

	public CannotFilterException() {
		super();
	}

	public CannotFilterException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public CannotFilterException(String message) {
		super(message);
		
	}

	public CannotFilterException(Throwable cause) {
		super(cause);
		
	}

}