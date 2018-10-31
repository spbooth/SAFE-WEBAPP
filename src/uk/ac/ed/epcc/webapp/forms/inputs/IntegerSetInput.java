//| Copyright - The University of Edinburgh 2011                            |
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
import java.util.LinkedHashSet;
import java.util.Set;

import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;

/** An integer input that selects an integer from a set as a pull down
 * 
 * @author spb
 *
 */


public class IntegerSetInput extends IntegerInput implements ListInput<Integer,Integer> {
    private final LinkedHashSet<Integer> values;
    
    public IntegerSetInput(int list[]){
    	this.values=new LinkedHashSet<>();
    	for(int i : list){
    		values.add(i);
    	}
    }
    public IntegerSetInput(Integer list[]){
    	this.values=new LinkedHashSet<>();
    	for(Integer i : list){
    		values.add(i);
    	}
    }
    public IntegerSetInput(Set<Integer> values){
    	this.values=new LinkedHashSet<>(values);
    }
	@Override
	public Integer getItem() {
		return getValue();
	}

	@Override
	public void setItem(Integer item) {
		setValue(item);
	}

	@Override
	public Integer getItembyValue(Integer value) {
		return value;
	}

	@Override
	public Iterator<Integer> getItems() {
		Number min = getMin();
		Number max = getMax();
		if( min != null || max != null) {
			// return items that are in range
			LinkedHashSet<Integer> tmp = new LinkedHashSet<>();
			for(Integer i : values) {
				if( (min == null || i.intValue() >= min.intValue()) &&
					(max == null || i.intValue() <= max.intValue())) {
					tmp.add(i);
				}
			}
			return tmp.iterator();
		}
		return values.iterator();
	}
	@Override
	public int getCount(){
		return values.size();
	}

	@Override
	public String getTagByItem(Integer item) {
		return item.toString();
	}

	@Override
	public String getTagByValue(Integer value) {
		return value.toString();
	}

	@Override
	public String getText(Integer item) {
		// use getString so we can control presented text using the NumberFormat
		return getString(item);
	}
	@Override
	public void validate() throws FieldException {
		super.validate();
		Integer value = getValue();
		if( value != null && ! values.contains(value)){
			throw new ValidateException("Value not in permitted set");
		}
	}
	@Override
	public <R> R accept(InputVisitor<R> vis) throws Exception {
		return vis.visitListInput(this);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.ListInput#isValid(java.lang.Object)
	 */
	@Override
	public boolean isValid(Integer item) {
		return values.contains(item);
	}

}