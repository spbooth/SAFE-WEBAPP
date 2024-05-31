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

import uk.ac.ed.epcc.webapp.validation.MaxLengthValidator;

/** An input that corresponds to a text box.
 * 
 * These methods allow fine-tuning of the appearance of the text box only.
 * Constraints on the permitted values should use a {@link MaxLengthValidator}
 * 
 * @author spb
 * @param <T> type of input
 *
 */
public interface LengthInput<T> extends ParseInput<T>{
	

	/**
	 * Get the input width for this Parameter as a text box.
	 * 
	 * @return int maximum input width
	 */
	public int getBoxWidth();


	/**
	 * set the input width for this parameter as a text box.
	 * This is the display width of the form field. longer inputs might be allowed
	 * 
	 * @param l
	 *            int maximum input width
	 */
	public void setBoxWidth(int l);
	
	/** should the input be a single line
	 * 
	 * @return true if input should be single line
	 */
	public boolean getSingle();
	

}