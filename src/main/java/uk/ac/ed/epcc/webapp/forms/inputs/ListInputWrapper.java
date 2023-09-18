//| Copyright - The University of Edinburgh 2019                            |
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

/**
 * @author Stephen Booth
 *
  * @param <V> type of value object
 * @param <T> type of Item object
 */
public abstract class ListInputWrapper<V, T> extends WrappingInput<V> implements ListInput<V,T>{

	/**
	 * @param nested
	 */
	public ListInputWrapper(ListInput<V, T> nested) {
		super(nested);
	}

	protected ListInput<V,T> getInner() {
		return (ListInput<V, T>) getNested();
	}

	@Override
	public T getItembyValue(V value) {
		return getInner().getItembyValue(value);
	}
	@Override
	public V getValueByItem(T item) throws TypeException {
		return getInner().getValueByItem(item);
	}

	@Override
	public Iterator<T> getItems() {
		return getInner().getItems();
	}

	@Override
	public int getCount() {
		return getInner().getCount();
	}

	@Override
	public String getTagByItem(T item) {
		return getInner().getTagByItem(item);
	}

	@Override
	public String getTagByValue(V value) {
		return getInner().getTagByValue(value);
	}

	@Override
	public V convert(Object v) throws TypeException {
		if( v == null){
			return null;
		}
		if( v instanceof String){
			if( ((String) v).trim().isEmpty()){
				return null;
			}
		}
		return getInner().convert(v);
	}


	@Override
	public T getItem() {
		return getInner().getItem();
	}

	@Override
	public void setItem(T item) {
		getInner().setItem(item);
	}

	@Override
	public boolean isValid(T item) {
		return getInner().isValid(item);
	}

}