package uk.ac.ed.epcc.webapp.forms.inputs;

import java.util.Iterator;

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
	 * 
	 * @param in
	 */
	public RadioButtonInput(ListInput<V,T> in){
		this(in,null);
		Iterator<T> it = in.getItems();
		if( it != null && it.hasNext()){
			setItem(it.next());
		}
	}
	/** Construct a RadioButtonInput with a specified default (can be null).
	 * 
	 * @param in
	 * @param default_sel
	 */
	public RadioButtonInput(ListInput<V,T> in,T default_sel){
		nested=in;
		// default to first entry
		setItem(default_sel);
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
	public void parse(String v) throws ParseException {
		// Use nested parse if we can
		if( nested instanceof ParseInput){
			((ParseInput<V>)nested).parse(v);
			return;
		}
		nested.setValue(nested.convert(v));
	}

}
