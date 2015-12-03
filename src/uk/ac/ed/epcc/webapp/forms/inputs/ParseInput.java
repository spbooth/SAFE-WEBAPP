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

import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;

/**
 * extended version of Input that supports parsing a String represenatation of
 * the input
 * 
 * @author spb
 * @param <T> type of input
 * 
 */
public interface ParseInput<T> extends Input<T> {
	/**
	 * get a String representation of the value in a form that is compatible
	 * with the way the input is parsed.
	 * @return String or null if no value
	 */
	public abstract String getString();

	
	/**
	 * Set the value of the input by parsing a textual representation of the
	 * input.
	 * 
	 * @param v
	 * @throws ParseException
	 */
	public abstract void parse(String v) throws ParseException;
}