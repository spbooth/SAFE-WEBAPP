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
	/** Parse a String into the correct type for this input.
	 * 
	 * This must be compatible with the {@link #getString(Object)} method.
	 * 
	 * @param v
	 * @return
	 * @throws ParseException
	 */
	
	public abstract T parseValue(String v) throws ParseException;
	
	/**
	 * get a String representation of the value in a form that is compatible
	 * with the way the input is parsed.
	 * @return String or null if no value
	 */
	public default String getString() {
		return getString(getValue());
	}

	
	/**
	 * Set the value of the input by parsing a textual representation of the
	 * input.
	 * 
	 * Normally this defaults to using {@link #parseValue(String)} and {@link #setValue(Object)}
	 * but it can be overridden for example if the natural parse mechanism generates an Item
	 * in an item input.
	 * @param v
	 * @throws ParseException
	 */
	public default void parse(String v) throws ParseException{
		setValue(parseValue(v));
	}
}