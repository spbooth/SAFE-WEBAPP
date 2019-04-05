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

import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
/** Adapter to convert a {@link ListInput} into a {@link OptionalListInput}
 * returning null on not selected.
 * This also allows the un-selected text to be customised.
 * @author spb
 *
 * @param <V> value type
 * @param <T> item type
 */
public class OptionalListInputWrapper<V,T> implements OptionalListInput<V, T> {
	private ListInput<V,T> inner;
	private String unselected="Not Selected";
	public OptionalListInputWrapper(ListInput<V,T> inner){
		this.inner=inner;
	}
	public OptionalListInputWrapper(ListInput<V,T> inner,String text){
		this(inner);
		unselected=text;
	}
	
	public T getItembyValue(V value) {
		return inner.getItembyValue(value);
	}
	public Iterator<T> getItems() {
		return inner.getItems();
	}
	public int getCount(){
		return inner.getCount();
	}
	public String getTagByItem(T item) {
		return inner.getTagByItem(item);
	}
	public String getTagByValue(V value) {
		return inner.getTagByValue(value);
	}
	public String getText(T item) {
		return inner.getText(item);
	}
	public String getKey() {
		return inner.getKey();
	}
	public V getValue() {
		return inner.getValue();
	}
	public void setKey(String key) {
		inner.setKey(key);
	}
	public V setValue(V v) throws TypeError {
		return inner.setValue(v);
	}
	public V convert(Object v) throws TypeError {
		if( v == null){
			return null;
		}
		if( v instanceof String){
			if( ((String) v).trim().length()==0){
				return null;
			}
		}
		return inner.convert(v);
	}
	public String getString(V value) {
		return inner.getString(value);
	}
	public String getPrettyString(V value) {
		return inner.getPrettyString(value);
	}
	public void validate() throws FieldException {
		inner.validate();		
	}
	public <R> R accept(InputVisitor<R> vis) throws Exception {
		return vis.visitListInput(this);
	}
	public T getItem() {
		return inner.getItem();
	}
	public void setItem(T item) {
		inner.setItem(item);
	}
	public String getUnselectedText() {
		return unselected;
	}
	public void setUnselectedText(String text) {
		unselected=text;
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.ListInput#isValid(java.lang.Object)
	 */
	@Override
	public boolean isValid(T item) {
		return inner.isValid(item);
	}
}