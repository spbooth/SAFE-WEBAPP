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
/*
 * Created on Nov 3, 2004 by spb
 *
 */
package uk.ac.ed.epcc.webapp.model.data.forms;

import uk.ac.ed.epcc.webapp.forms.inputs.CheckBoxInput;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;

/**
 * CheckboxSelector Generates a checkbox selector for DataObject edit forms
 * 
 * @author spb
 * 
 */


public class CheckboxSelector implements Selector<Input<String>> {
	String checked_tag;

	String unchecked_tag;

	/**
	 * Default constructor for Y/N tags
	 * 
	 * 
	 */
	public CheckboxSelector() {
		this("Y", "N");
	}

	/**
	 * create a selector with customised tags.
	 * 
	 * Note this can only be used to represent a boolean if the checked tag
	 * auto-converts to boolean true
	 * 
	 * @param checked_tag
	 *            the String value corresponding to a checked box
	 * @param unchecked_tag
	 *            the String value corresponding to a unchecked box
	 * 
	 * 
	 */
	public CheckboxSelector(String checked_tag, String unchecked_tag) {
		super();
		this.checked_tag = checked_tag;
		this.unchecked_tag = unchecked_tag;
	}

	@Override
	public Input<String> getInput() {
		return new CheckBoxInput(checked_tag, unchecked_tag);
	}

}