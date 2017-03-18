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

import java.util.Map;

import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;

/** Equivalent of ParseInput for MultiInputs 
 * If an Input implements this interface then it wants to directly parse a
 * map of all form inputs because of interactions between the sub-inputs.
 * @see AlternateInput  
 * 
 * @author spb
 *
 */
public interface ParseMapInput {
	/**
	 * get a map of String representation of the values in the input that is compatible
	 * with the way the input parses the map
	 * @return Map or null if no value
	 */
	public abstract Map<String,Object> getMap();

	
	/**
	 * Set the value of the input by parsing a textual representation of the
	 * sub inputs.
	 * 
	 * @param v
	 * @return true if non leaf value used.
	 * @throws ParseException
	 */
	public abstract boolean parse(Map<String,Object> v) throws ParseException;
}