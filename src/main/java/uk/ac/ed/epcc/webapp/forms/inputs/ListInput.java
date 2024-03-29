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
public abstract interface ListInput<V,T> extends  SuggestedItemInput<V,T> {

	

	
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
	
	/** Get a group name for the option
	 * If supported this collects the values in the input into
	 * groups (eg. using html optgroup)
	 * All items in the same group should occur contiguously in the
	 * item list. A null value (the default) indicates no grouping
	 * 
	 * @param item
	 * @return group label or null.
	 */
	public default String getGroup(T item) {
		return null;
	}
	/** optionally generate hover text for an item.
	 * 
	 * @param item
	 * @return
	 */
	public default String getTooltip(T item) {
		return null;
	}
}