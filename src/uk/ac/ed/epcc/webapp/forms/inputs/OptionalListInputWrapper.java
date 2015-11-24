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
	private String unselected="Not selected";
	private boolean is_optional=true;
	public OptionalListInputWrapper(ListInput<V,T> inner){
		this.inner=inner;
		if( inner instanceof OptionalInput){
			((OptionalInput)inner).setOptional(true);
		}
	}
	public OptionalListInputWrapper(ListInput<V,T> inner,String text){
		this(inner);
		unselected=text;
	}
	public boolean isOptional() {
		if( inner instanceof OptionalInput){
			return ((OptionalInput)inner).isOptional();
		}
		return is_optional;
	}
	public void setOptional(boolean opt) {
		if( inner instanceof OptionalInput){
			((OptionalInput)inner).setOptional(opt);
		}
		is_optional=opt;
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
		if( isOptional() && inner.getValue() == null){
			return;
		}
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
}
