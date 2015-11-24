// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.inputs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;

/** A multi input consists of a combination of sub inputs
 * Optionally each input can have an associated text label
 * 
 * 
 * @author spb
 * @param <V> type of input we are constructing
 * @param <I> Type of sub inputs
 *
 */
public abstract class MultiInput<V,I extends Input> implements Input<V> {
	// map between sub-keys and inputs.
	private Map<String,I> m;
	// map between sub-keys and labels
    private Map<String,String> labels;
	private String key = "default";
	private boolean line_breaks=false;
	public MultiInput(){
		m = new LinkedHashMap<String,I>();
		labels = new HashMap<String,String>();
	}
	public final void addInput(String sub_key, I i) {
		addInput(sub_key,null,i);
	}
	protected void addInput(String sub_key,String label, I i){
		i.setKey(getKey() + "." + sub_key);
		m.put(sub_key, i);
		labels.remove(sub_key); // remove in case we are re-setting
		if( label != null ){
			labels.put(sub_key, label);
		}
	}
	public boolean containsInput(Class<? extends Input> c) {
		for (Iterator<I> it = getInputs(); it.hasNext();) {
			Input<?> i =  it.next();
			if (c.isAssignableFrom(i.getClass())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * get all of the sub-selectors
	 * 
	 * @return Iterator over sub-selectors
	 */
	public final Iterator<I> getInputs() {
		return m.values().iterator();
	}
	public final Set<String> getSubKeys(){
		return m.keySet();
	}
    public final I getInput(String sub_key){
    	return m.get(sub_key);
    }
    public final String getSubLabel(String sub_key){
    	return labels.get(sub_key);
    }
	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.epcc.webapp.model.data.forms.Selector#getKey()
	 */
	public final String getKey() {
		return key;
	}

	public abstract V getValue();
	public final void removeInput(String sub_key) {
		m.remove(sub_key);
		labels.remove(sub_key);
	}
	public final String getString(){
		return getString(getValue());
	}
	  public String getString(V val){
	    	if( val == null ){
	    		return null;
	    	}
	    	return val.toString();
	    }
	    
	    public String getPrettyString(V val){
	    	if( val == null ){
	    		return "no value";
	    	}
	    	return getString(val);
	    }
	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.epcc.webapp.model.data.forms.Selector#setKey(java.lang.Object)
	 */
	public final void setKey(String key) {
		this.key = key;
		/*
		 * Also change the key mappings for any sub
		 * 
		 */
		for (Iterator it = m.keySet().iterator(); it.hasNext();) {
			String sub_key = (String) it.next();
			I i =  m.get(sub_key);
			i.setKey(key + "." + sub_key);
		}
	}
	public abstract V setValue(V v) throws TypeError;
	public <R> R accept(InputVisitor<R> vis) throws Exception {
		return vis.visitMultiInput(this);
	}
	public void validate() throws FieldException {
		// default behaviour is to validate each sub input
		// sub-classes can override.
		for(I i : m.values()){
			i.validate();
		}
	}
	public boolean hasLineBreaks() {
		return line_breaks;
	}
	public boolean hasSubLabels(){
		return labels.size() > 0;
	}
	public void setLineBreaks(boolean line_breaks) {
		this.line_breaks = line_breaks;
	}
}