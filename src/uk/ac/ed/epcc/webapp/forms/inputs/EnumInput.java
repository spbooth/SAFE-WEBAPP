// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.inputs;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;

/** Input to select values from a Java Enum 
 * the DB will contain the Enum name and the menu the result of the toString call.
 * 
 * @author spb
 *
 * @param <E> Type of Enum to use
 */
@uk.ac.ed.epcc.webapp.Version("$Id: EnumInput.java,v 1.5 2014/09/15 14:30:19 spb Exp $")

public class EnumInput<E extends Enum<E>> extends TextInput implements  ListInput<String,E>,OptionalListInput<String, E>,PreSelectInput<String, E> {
    EnumSet<E> set;
    Map<String,E> lookup;
    private String unslected_text=null;
    private boolean allow_preselect=true;
    public EnumInput(EnumSet<E> set){
    	super(true);
    	this.set = set;
    	lookup = new HashMap<String,E>();
    	for(E s: set){
    		lookup.put(s.name(), s);
    	}
    }
    public EnumInput(Class<E> clazz){
    	this(EnumSet.allOf(clazz));
    }
	public E getItembyValue(String value) {
		if( value == null ){
			return null;
		}
		return lookup.get(value);
	}

	public Iterator<E> getItems() {
		return set.iterator();
	}
	
	public int getCount(){
		return set.size();
	}

	public String getTagByItem(E item) {
		if( item == null){
			return null;
		}
		return item.name();
	}

	public String getTagByValue(String value) {
		return getTagByItem(getItembyValue(value));
	}

	public String getText(E item) {
		if( item == null ){
			return null;
		}
		return item.toString();
	}

	public E getItem() {
		String val = getValue();
		if( val == null ){
			return null;
		}
		return getItembyValue(val);
	}

	
	public void setItem(E v) {
		if( v == null ){
			setValue(null);
		}
		setValue(getTagByItem(v));
	}
	
	@Override
	public String getPrettyString(String val) {
		if( val == null ){
			return "No Value";
		}
		return getText(getItembyValue(val));
	}
	@SuppressWarnings("unchecked")
	@Override
	public String convert(Object v) throws TypeError {
		if( v == null ){
			return null;
		}
		if( set.contains(v)){
			return getTagByItem((E) v);
		}
		return super.convert(v);
	}
	
	@Override
	public <R> R accept(InputVisitor<R> vis) throws Exception {
		return vis.visitListInput(this);
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.PreSelectInput#allowPreSelect()
	 */
	public boolean allowPreSelect() {
		return allow_preselect;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.PreSelectInput#setPreSelect(boolean)
	 */
	public void setPreSelect(boolean value) {
		allow_preselect=value;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.OptionalListInput#getUnselectedText()
	 */
	public String getUnselectedText() {
		return unslected_text;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.OptionalListInput#setUnselectedText(java.lang.String)
	 */
	public void setUnselectedText(String text) {
		unslected_text=text;
		
	}
	@Override
	public void validate() throws FieldException {
		super.validate();
		String value = getValue();
		if( value != null && ! lookup.containsKey(value)){
			throw new ValidateException("Value not permitted");
		}
	}

}