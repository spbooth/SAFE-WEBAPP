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
package uk.ac.ed.epcc.webapp.model.data.forms;

import uk.ac.ed.epcc.webapp.forms.inputs.Input;

/**
 * Selector Used to provide an override form selector for a property in a form.
 * For example a pull-down menu. Normally we will have the Factory classes
 * implement this interface to provide a selector for the objects they create.
 * The same selector should be used to both generate the HTML form fragment and
 * to parse the resulting input. Note that a useful convension is to use the
 * name parameter as the name of the form input. If there are multiple form
 * inputs for a single selector then the names should be derived from the name
 * parameter.
 * 
 * @author spb
 * @param <T> type of input
 * 
 */
@FunctionalInterface
public interface Selector<T extends Input> {

	/**
	 * get the Input associated with this object.
	 * 
	 * @return Input
	 */
	public abstract T getInput();
}