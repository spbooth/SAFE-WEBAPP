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

import java.util.Set;

/**
 * Input that selects multiple values from a (relatively small) set. For consistency the parse method should
 * parse a comma separated list of tag values. That is tags are String representations of the Input values.
 * 
 * Each possible choice has 3 representations
 * <ul>
 * <li> The Item is the target object e.g. the DataObject being selected. We
 * sometimes need to call methods on these. For a simple list the Value and Item
 * may be the same.
 * <li> The tag which is a simple string used to identify the selection this can
 * be used internally e.g. for HTML forms.
 * <li> The text actually presented to the user.
 * </ul>
 * 
 * @author spb
 * @param <T> type of Item object
 * 
 */
public abstract interface MultiValueInput<T> extends ParseInput<String>, ItemInput<String, Set<T>>	{

	
	/**
	 * get an identifying tag string from the domain object
	 * 
	 * @param item
	 * @return String tag
	 */
	public abstract String getTagByItem(T item);
	
	/** get the 
	 * 
	 * @param tag
	 * @return
	 */
	public abstract T getItemByTag(String tag);

	
	/**
	 * get the user presented text from the domain object
	 * @param item
	 * @return String user text
	 */
	public abstract String getText(T item);
	
	
}