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
package uk.ac.ed.epcc.webapp.forms.inputs;
/** Error thrown if the value passed to an input has no defined 
 * convertion to the expected type.
 * 
 * @author spb
 *
 */


public class TypeError extends Error {

	public TypeError() {
	}

	public TypeError(String message) {
		super(message);
	}
	public TypeError(Class t){
		super("Cannot convert type "+t.getCanonicalName());
	}

	public TypeError(Throwable cause) {
		super(cause);
	}

	public TypeError(String message, Throwable cause) {
		super(message, cause);
	}

}