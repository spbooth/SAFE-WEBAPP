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
package uk.ac.ed.epcc.webapp.model.data.Exceptions;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;

/**
 * Exception thrown when a query Fails to find a matching BasicDataObject
 * 
 * @author spb
 * 
 * 
 */


public class DataNotFoundException extends DataException {
	/**
	 * @param str
	 * @param cause
	 */
	public DataNotFoundException(String str, Throwable cause) {
		super(str, cause);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param str
	 */
	public DataNotFoundException(String str) {
		super(str);
	}

}