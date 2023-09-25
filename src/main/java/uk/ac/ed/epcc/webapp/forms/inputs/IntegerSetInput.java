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

import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.validation.FieldValidator;

/** An integer input that selects an integer from a set as a pull down
 * 
 * @author spb
 *
 */


public class IntegerSetInput extends AbstractInput<Integer> implements ListInput<Integer,Integer> {
    /**
	 * @author Stephen Booth
	 *
	 */
	public final class IntegerSetValidator implements FieldValidator<Integer> {
		/**
		 * 
		 */
		private final Set<Integer> values;

		/**
		 * @param values
		 */
		public IntegerSetValidator(Set<Integer> values) {
			this.values = values;
		}

		@Override
		public void validate(Integer value) throws FieldException {
			if( value != null && ! values.contains(value)){
				throw new ValidateException("Value not in permitted set");
			}
			
		}
	}
	private final LinkedHashSet<Integer> values;
    
    public IntegerSetInput(int list[]){
    	this.values=new LinkedHashSet<>();
    	for(int i : list){
    		values.add(i);
    	}
    	addValidator(new IntegerSetValidator(values));
    }
    public IntegerSetInput(Integer list[]){
    	this.values=new LinkedHashSet<>();
    	for(Integer i : list){
    		values.add(i);
    	}
    	addValidator(new IntegerSetValidator(values));
    }
    public IntegerSetInput(Set<Integer> values){
    	this.values=new LinkedHashSet<>(values);
    	addValidator(new IntegerSetValidator(values));
    }
	@Override
	public Integer getItem() {
		return getValue();
	}

	@Override
	public Integer getValueByItem(Integer item) {
		return item;
	}

	@Override
	public Integer getItembyValue(Integer value) {
		return value;
	}

	@Override
	public Iterator<Integer> getItems() {

		// return items that are valid
		LinkedHashSet<Integer> tmp = new LinkedHashSet<>();
		for(Integer i : values) {
			try {
				validate(i);
				tmp.add(i);
			}catch(FieldException e) {

			}
		}
		return tmp.iterator();


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
	@Override
	public Integer getItemByTag(String tag) {
		return Integer.parseInt(tag);
	}
	@Override
	public Integer getValueByTag(String tag) {
		return Integer.parseInt(tag);
	}

	public void setInteger(int i) {
		try {
			setValue(i);
		} catch (TypeException e) {
			throw new ConsistencyError("Impossible type error", e);
		}
	}
}