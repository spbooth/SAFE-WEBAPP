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

import uk.ac.ed.epcc.webapp.forms.FieldValidator;
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
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
public class RadioButtonInput<V, T> implements ListInput<V, T>, ParseInput<V> {
	private final ListInput<V,T> nested;
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
		nested=in;
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

	public String getKey() {
		return nested.getKey();
	}

	public V getValue() {
		return nested.getValue();
	}

	public void setKey(String key) {
		nested.setKey(key);
	}

	public V setValue(V v) throws TypeError {
		return nested.setValue(v);
	}

	public V convert(Object v) throws TypeError {
		return nested.convert(v);
	}

	public String getString(V value) {
		return nested.getString(value);
	}

	public String getPrettyString(V value) {
		return nested.getPrettyString(value);
	}

	public void validate() throws FieldException {
		nested.validate();

	}

	public <R> R accept(InputVisitor<R> vis) throws Exception {
		return vis.visitRadioButtonInput(this);
	}

	public T getItem() {
		return nested.getItem();
	}

	public void setItem(T item) {
		nested.setItem(item);

	}

	public T getItembyValue(V value) {
		return nested.getItembyValue(value);
	}

	public Iterator<T> getItems() {
		return nested.getItems();
	}
	public int getCount(){
		return nested.getCount();
	}
	public String getTagByItem(T item) {
		return nested.getTagByItem(item);
	}

	public String getTagByValue(V value) {
		return nested.getTagByValue(value);
	}

	public String getText(T item) {
		return nested.getText(item);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.ParseInput#getString()
	 */
	public String getString() {
		if( nested instanceof ParseInput){
			return ((ParseInput<V>)nested).getString();
		}
		return getValue().toString();
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.ParseInput#parse(java.lang.String)
	 */
	public V parseValue(String v) throws ParseException {
		// Use nested parse if we can
		if( nested instanceof ParseInput){
			return ((ParseInput<V>)nested).parseValue(v);
		}
		return nested.convert(v);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.ListInput#isValid(java.lang.Object)
	 */
	@Override
	public boolean isValid(T item) {
		return nested.isValid(item);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.Input#addValidator(uk.ac.ed.epcc.webapp.forms.FieldValidator)
	 */
	@Override
	public void addValidator(FieldValidator<V> val) {
		nested.addValidator(val);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.Input#removeValidator(uk.ac.ed.epcc.webapp.forms.FieldValidator)
	 */
	@Override
	public void removeValidator(FieldValidator<V> val) {
		nested.removeValidator(val);
		
	}
	@Override
	public String toString() {
		return "RadioButtonInput [" + nested + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nested == null) ? 0 : nested.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RadioButtonInput other = (RadioButtonInput) obj;
		if (nested == null) {
			if (other.nested != null)
				return false;
		} else if (!nested.equals(other.nested))
			return false;
		return true;
	}

}