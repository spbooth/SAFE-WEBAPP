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
 * an input that has some Domain Object associated with its values
 * 
 * @author spb
 * @param <V> type of input
 * @param <T> type of item object
 * 
 */
public interface ItemInput<V,T> extends Input<V>{
	
	/**
	 * get the domain object from the Input value if defined
	 * 
	 * @param value
	 *            input Value
	 * @return Object the domain object or null
	 */
	public abstract T getItembyValue(V value);
	/**
	 * get the domain Object associated with the current value
	 * 
	 * @return Object
	 */
	default public  T getItem() {
		return getItembyValue(getValue());
	}

	/**
	 * Set the value of the input using an item
	 * 
	 * @param item
	 */
	public abstract void setItem(T item);
}