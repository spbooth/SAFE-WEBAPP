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

/** Adapter to convert a {@link ListInput} into a {@link OptionalListInput}
 * returning null on not selected.
 * This also allows the un-selected text to be customised.
 * @author spb
 *
 * @param <V> value type
 * @param <T> item type
 */
public class OptionalListInputWrapper<V,T> extends ListInputWrapper<V, T> implements OptionalListInput<V, T> {
	String unselected="Not Selected";
	public OptionalListInputWrapper(ListInput<V,T> inner){
		super(inner);
	}
	public OptionalListInputWrapper(ListInput<V,T> inner,String text){
		this(inner);
		unselected=text;
	}
	
	@Override
	public String getText(T item) {
		return getInner().getText(item);
	}
	@Override
	public V convert(Object v) throws TypeException {
		if( v == null){
			return null;
		}
		if( v instanceof String){
			if( ((String) v).trim().length()==0){
				return null;
			}
			if( unselected.equals(v)) {
				return null;
			}
		}
		return getInner().convert(v);
	}
	
	@Override
	public String getUnselectedText() {
		return unselected;
	}
	@Override
	public void setUnselectedText(String text) {
		unselected=text;
		
	}
}