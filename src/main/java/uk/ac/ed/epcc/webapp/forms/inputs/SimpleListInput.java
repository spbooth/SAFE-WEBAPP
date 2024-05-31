package uk.ac.ed.epcc.webapp.forms.inputs;

import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;

/** Abstract superclass for {@link ListInput}s where the tags and values are the same.
 * 
 * State is held as the String values.
 * 
 * @param <T> item type
 * @see StringListInput
 * @see CodeListInput
 */
public abstract class SimpleListInput<T> extends AbstractStringInput implements ListInput<String, T> {

	public SimpleListInput() {
		
	}

	@Override
	public final <R> R accept(InputVisitor<R> vis) throws Exception {
		return vis.visitListInput(this);
	}

	

	

	@Override
	public final String getValueByItem(T item) throws TypeException {
		return getTagByItem(item);
	}
    @Override
	public final T getItembyValue(String value) {
    	return getItemByTag(value);
    }

	@Override
	public final String getTagByValue(String value) {
		return value;
	}
	@Override
	public final String getValueByTag(String tag) {
		return tag;
	}
	@Override
	public final String getString(String val){
    	return val;
    }

	@Override
	public String getText(T item) {
		return getTagByItem(item);
	}

	@Override
	public final String convert(Object v) throws TypeException  {
		if( v == null ){
			return null;
		}
		if( v instanceof String){
			return (String)v;
		}
		throw new TypeException(v.getClass());
	}
}
