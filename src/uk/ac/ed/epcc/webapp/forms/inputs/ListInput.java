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

import java.util.Iterator;


/**
 * Input that selects values from a List. For consistency the parse method should
 * parse tag values. That is tags are String representations of the Input values.
 * 
 * Each possible choice has 4 representations
 * <ul>
 * <li> The Value corresponding to the value stored in the Input
 * <li> The Item is the target object e.g. the DataObject being selected. We
 * sometimes need to call methods on these. For a simple list the Value and Item
 * may be the same.
 * <li> The tag which is a simple string used to identify the selection this can
 * be used internally e.g. for HTML forms.
 * <li> The text actually presented to the user.
 * </ul>
 * 
 * @author spb
 * @param <V> type of value object
 * @param <T> type of Item object
 * 
 */
public abstract interface ListInput<V,T> extends Input<V>, ItemInput<T> {

	/**
	 * get the domain object from the Input value if defined
	 * 
	 * @param value
	 *            input Value
	 * @return Object the domain object or null
	 */
	public abstract T getItembyValue(V value);

	/**
	 * get a list of domain objects that are being selected
	 * 
	 * @return Iterator
	 */
	public abstract Iterator<T> getItems();

	/** get the number of valid choices in the list.
	 * null selections don't count.
	 * 
	 * @return int
	 */
	public int getCount();
	/**
	 * get an identifying tag string from the domain object
	 * 
	 * @param item
	 * @return String tag
	 */
	public abstract String getTagByItem(T item);
	
	/** Test if an object of the Item type is one of the possible items.
	 * 
	 * @param item
	 * @return boolean
	 */
	public boolean isValid(T item);

	/**
	 * get the tag string from the actual value of the input
	 * 
	 * @param value
	 * @return String tag
	 */
	public abstract String getTagByValue(V value);

	/**
	 * get the user presented text from the domain object
	 * may return null if not selected
	 * @param item
	 * @return String user text
	 */
	public abstract String getText(T item);

}