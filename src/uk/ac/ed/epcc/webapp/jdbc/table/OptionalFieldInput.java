package uk.ac.ed.epcc.webapp.jdbc.table;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
import uk.ac.ed.epcc.webapp.forms.inputs.InputVisitor;
import uk.ac.ed.epcc.webapp.forms.inputs.ItemInput;
import uk.ac.ed.epcc.webapp.forms.inputs.ListInput;
import uk.ac.ed.epcc.webapp.forms.inputs.ParseAbstractInput;
import uk.ac.ed.epcc.webapp.model.data.Repository;
/** Select a field from an option map that does not already exist
 * in a Repository.
 * The Value of this input is the field name.
 * This is also an {@link ItemInput} and will return the corresponding
 * element from the option map as the Item.
 * 
 * @author spb
 * @param <I> 
 *
 */
public class OptionalFieldInput<I> extends ParseAbstractInput<String> implements ListInput<String, I>{
	

	Map<String,I> fields;
	public OptionalFieldInput(Repository res, Map<String,I> options){
		fields = new HashMap<String, I>();
		for(String name : options.keySet()){
			if( ! res.hasField(name)){
				fields.put(name, options.get(name));
			}
		}
	}
	@Override
	public <R> R accept(InputVisitor<R> vis) throws Exception {
		return vis.visitListInput(this);
	}
	
	public I getItem() {
		
		return fields.get(getValue());
	}

	public void setItem(I item) {
		setValue(getTagByItem(item));
		
	}

	
	public void parse(String v) throws ParseException {
		if( fields.containsKey(v)){
			setValue(v);
			return;
		}
		throw new ParseException("Illegal value "+v);
	}

	
	public I getItembyValue(String value) {
		return fields.get(value);
	}

	
	public Iterator<I> getItems() {
		return fields.values().iterator();
	}

	public int getCount(){
		return fields.size();
	}
	
	public String getTagByItem(I item) {
		for(String name : fields.keySet()){
			if( fields.get(name).equals(item)){
				return name;
			}
		}
		return null;
	}

	
	public String getTagByValue(String value) {
		return value;
	}

	
	public String getText(I item) {
		return getTagByItem(item);
	}

}
