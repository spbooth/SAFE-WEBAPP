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
public class OptionalIndexInput<I> extends ParseAbstractInput<String> implements ListInput<String, I>{
	

	Map<String,I> indexes;
	public OptionalIndexInput(Repository res, Map<String,I> options){
		indexes = new HashMap<>();
		for(String name : options.keySet()){
			if( ! res.hasIndex(name)){
				indexes.put(name, options.get(name));
			}
		}
	}
	@Override
	public <R> R accept(InputVisitor<R> vis) throws Exception {
		return vis.visitListInput(this);
	}
	
	@Override
	public I getItem() {
		
		return indexes.get(getValue());
	}

	@Override
	public void setItem(I item) {
		setValue(getTagByItem(item));
		
	}

	
	@Override
	public void parse(String v) throws ParseException {
		if( indexes.containsKey(v)){
			setValue(v);
			return;
		}
		throw new ParseException("Illegal value "+v);
	}

	
	@Override
	public I getItembyValue(String value) {
		return indexes.get(value);
	}

	
	@Override
	public Iterator<I> getItems() {
		return indexes.values().iterator();
	}

	@Override
	public int getCount(){
		return indexes.size();
	}
	
	@Override
	public String getTagByItem(I item) {
		for(String name : indexes.keySet()){
			if( indexes.get(name).equals(item)){
				return name;
			}
		}
		return null;
	}

	
	@Override
	public String getTagByValue(String value) {
		return value;
	}

	
	@Override
	public String getText(I item) {
		return getTagByItem(item);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.ListInput#isValid(java.lang.Object)
	 */
	@Override
	public boolean isValid(I item) {
		return indexes.containsValue(item);
	}

}