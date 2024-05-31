//| Copyright - The University of Edinburgh 2015                            |
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
package uk.ac.ed.epcc.webapp.forms.inputs;

import java.util.Iterator;
/** Wrapper class to convert a ListInput to be displayed as a
 * RadioButton. The input is assumed not to be optional.
 * 
 * Note that this class implements {@link ParseInput}. If the nested input does not
 * also implement this interface then the {@link #convert(Object)} method needs to 
 * be able to convert the String representation of the value.
 * 
 * @author spb
 *
 * @param <V> type of value
 * @param <T> type of item
 */
public class RadioButtonInput<V, T> extends ListInputWrapper<V, T> {
	/** Construct a RadioButtonInput defaulting to the first selectable item
	 * unless there is already one set in the {@link ListInput}
	 * 
	 * @param in
	 */
	public RadioButtonInput(ListInput<V,T> in){
		this(in,null);
		
	}
	/** Construct a RadioButtonInput with a specified default (can be null).
	 * If null is specified the existing item is kept, if there is no existing 
	 * default first value from the ListInput is chosen
	 * 
	 * @param in
	 * @param default_sel
	 */
	public RadioButtonInput(ListInput<V,T> in,T default_sel){
		super(in);
		if( default_sel != null ) {
			setItem(default_sel);
		}else {
			T item = in.getItem();
			if(item ==null) {
				// If we don't already have an item default to first one
				Iterator<T> it = in.getItems();
				if( it != null && it.hasNext()){
					setItem(it.next());
				}
				if( it instanceof AutoCloseable) {
					try {
						// 
						((AutoCloseable)it).close();
					} catch (Exception e) {
					}
				}
			}
		}
	}

	
	
	@Override
	public final <R> R accept(InputVisitor<R> vis) throws Exception {
		return vis.visitRadioButtonInput(this);
	}

	

	

	@Override
	public String toString() {
		return "RadioButtonInput [" + getInner() + "]";
	}
	

}