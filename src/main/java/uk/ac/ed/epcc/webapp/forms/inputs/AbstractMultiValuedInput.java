package uk.ac.ed.epcc.webapp.forms.inputs;

import java.util.LinkedHashSet;
import java.util.Set;

import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;

/** An abstract supercalss for {@link MultiInput}s that implement {@link MultiValueInput}
 * 
 * @author Stephen Booth
 *
 * @param <T> type of item
 */
public abstract  class AbstractMultiValuedInput<T> extends MultiInput<String, BooleanInput> implements MultiValueInput<T>{
    private final Set<T> allowed;
	public AbstractMultiValuedInput(Set<T> allowed) {
		setLineBreaks(true);
		this.allowed=allowed;
		for(T item : allowed) {
			addInput(getTagByItem(item), getText(item), new BooleanInput());
		}
	}
	@Override
	public String parseValue(String v) throws ParseException {
		if( v == null || v.isEmpty()) {
			return v;
		}
		Set<String> tags = new LinkedHashSet<String>();
		for( String t : v.split("\\s*,\\s*")) {
			if( getInput(t) != null) {
				tags.add(t);
			}
		}
		return String.join(",", tags);
	}
	@Override
	public void parse(String v) throws ParseException {
		setNull();
		if( v == null || v.isEmpty()) {
			return;
		}
	
		for( String t : v.split("\\s*,\\s*")) {
			BooleanInput input = getInput(t);
			if( input != null) {
				input.setChecked(true);
			}
		}
	}
	@Override
	public Set<T> getItembyValue(String value) {
		LinkedHashSet<T> result = new LinkedHashSet<T>();
		if( value != null) {
			for(String tag : value.split("\\s*,\\s*")) {
				T item = getItemByTag(tag);
				if(allowed.contains(item)) {
					result.add(item);
				}
			}
		}
		return result;
	}
	@Override
	public void setItem(Set<T> item) {
		for(T i : allowed) {
			getInput(getTagByItem(i)).setChecked(item.contains(i));
		}
		
	}
	@Override
	public Set<T> getItem() {
		LinkedHashSet<T> result = new LinkedHashSet<T>();
		for(T i : allowed) {
			if(getInput(getTagByItem(i)).isChecked()){
				result.add(i);
			}
		}
		return result;
	}
	
	
	@Override
	public String getValue() {
		Set<String> tags = new LinkedHashSet<String>();
		for(T i : allowed) {
			String tag = getTagByItem(i);
			if(getInput(tag).isChecked()){
				tags.add(tag);
			}
		}
		return String.join(",", tags);
	}
	@Override
	public String setValue(String v) throws TypeException {
		
		if( v == null || v.isEmpty()) {
			String prev = getValue();
			setNull();
			return prev;
		}
		Set<String> new_tags = new LinkedHashSet<String>();
		Set<String> prev_tags = new LinkedHashSet<String>();
		for( String t : v.split("\\s*,\\s*")) {
				new_tags.add(t);
		}
		for(T i : allowed) {
			String tag = getTagByItem(i);
			BooleanInput input = getInput(tag);
			if( input.isChecked()) {
				prev_tags.add(tag);
			}
			input.setChecked(new_tags.contains(tag));
		}
		return String.join(",", prev_tags);
	}
	@Override
	public String getText(T item) {
		// default to using the tag as the label
		return getTagByItem(item);
	}
	

}
