// Copyright - The University of Edinburgh 2011
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
@uk.ac.ed.epcc.webapp.Version("$Id: IntegerSetInput.java,v 1.3 2014/09/15 14:30:20 spb Exp $")

public class IntegerSetInput extends IntegerInput implements ListInput<Integer,Integer> {
    private final LinkedHashSet<Integer> values;
    
    public IntegerSetInput(int list[]){
    	this.values=new LinkedHashSet<Integer>();
    	for(int i : list){
    		values.add(i);
    	}
    }
    public IntegerSetInput(Integer list[]){
    	this.values=new LinkedHashSet<Integer>();
    	for(Integer i : list){
    		values.add(i);
    	}
    }
    public IntegerSetInput(Set<Integer> values){
    	this.values=new LinkedHashSet<Integer>(values);
    }
	public Integer getItem() {
		return getValue();
	}

	public void setItem(Integer item) {
		setValue(item);
	}

	public Integer getItembyValue(Integer value) {
		return value;
	}

	public Iterator<Integer> getItems() {
		return values.iterator();
	}
	public int getCount(){
		return values.size();
	}

	public String getTagByItem(Integer item) {
		return item.toString();
	}

	public String getTagByValue(Integer value) {
		return value.toString();
	}

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

}