// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.Table;
import uk.ac.ed.epcc.webapp.forms.action.FormAction;
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.MissingFieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.inputs.ItemInput;
import uk.ac.ed.epcc.webapp.forms.inputs.LockedInput;
import uk.ac.ed.epcc.webapp.forms.inputs.OptionalInput;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;

/**
 * superclass for all Forms. A Form is functionally an ordered set of {@link Field}s. Once
 * built a Form can be used as a Map between field names and values. The methods
 * used to actually edit a form are specific to the form sub-class however this class
 * can be instantiated directly for testing.
 * 
 *<p>
 *A Form may either be used as just a collection of fields with associated validation ie as a component
 *of a larger composite form or may contain a set of {@link FormAction} classes to define an action to take on form submission.
 * @author spb
 
 * 
 */
@uk.ac.ed.epcc.webapp.Version("$Id: BaseForm.java,v 1.11 2014/12/28 13:12:42 spb Exp $")

public class BaseForm implements Form {

	private LinkedHashMap<String,Field> fields;

	private LinkedHashMap<String,FormAction> actions;
    private HashMap<String,String> tooltips;
    private HashMap<String,String> shortcuts;
	protected Set<FormValidator> validators = new HashSet<FormValidator>();
	

	private AppContext conn;
	protected Logger log;

	
	public BaseForm(AppContext c) {
		fields = new LinkedHashMap<String,Field>();
		actions = new LinkedHashMap<String,FormAction>();
		tooltips = new HashMap<String,String>();
		shortcuts = new HashMap<String,String>();
		conn = c;
		log = c.getService(LoggerService.class).getLogger(getClass());
	}

	/**
	 * add an FormAction to this Form
	 * 
	 * @param name
	 *            String action-name/button-text
	 * @param action
	 *            FormAction
	 */
	public void addAction(String name, FormAction action) {
		name=name.trim(); 
		actions.put(name, action);
		String tip = action.getHelp();
		if( tip != null ){
			tooltips.put(name, tip);
		}
		String shortcut = action.getShortcut();
		if( shortcut != null ){
			if( shortcuts.containsValue(shortcut)){
				conn.error("Duplicate shortcut in form:"+shortcut);
			}
			shortcuts.put(name, shortcut);
		}
	}

	/**
	 * Add and input to the next slot in the form
	 * 
	 * @param key
	 *            key to use to refer to field
	 * @param label
	 *            String to display to user
	 * @param s
	 *            Input to add
	 * @return Field object created
	 */
	public <I> Field addInput(String key, String label, Input<I> s) {
		if( s == null ){
			return null;
		}
		if (label == null) {
			label = key;
		}
		Field f =  makeField(key, label, s);
		s.setKey(key);
		fields.put(key, f);
		return f;
	}

	public void clear() {
		fields.clear();
		actions.clear();
		validators.clear();;
	}

	/**
	 * Check a form to see if it contains an input of a particular type.
	 * 
	 * @param c
	 *            Class for the Input type to be testes
	 * @return bioolean true if input exists
	 */
	public boolean containsInput(Class<? extends Input> c) {
		for (Iterator it = fields.values().iterator(); it.hasNext();) {
			Field<?> f = (Field) it.next();
			if (f.containsInput(c)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * perform a named action on this form. This also checks the form is
	 * validated
	 * 
	 * @param name
	 *            String name of action
	 * @return FormResult returned by action
	 * @throws FieldException
	 * @throws ActionException
	 */
	public FormResult doAction(String name) throws FieldException, ActionException {
		
		FormAction action = actions.get(name);
		if (action == null) {
			throw new ActionException("Unknown action");
		}
		if (action.getMustValidate() && !validate()) {
			throw new ActionException("Form state not valid");
		}
		return action.action(this);
	}
	public FormAction getAction(String name){
		return actions.get(name);
	}
	/**
	 * get the value associated with a Form field.
	 * Non existent fields always return null.
	 * @param key
	 * @return value
	 */
	public Object get(String key) {
		Field f = getField(key);
		if (f == null) {
			return null;
		}
		return f.getValue();
	}

	public Object getItem(String key){
		Input sel = getInput(key);
		if( sel == null ){
			return null;
		}
		if( sel instanceof LockedInput){
			sel = ((LockedInput)sel).getNested();
		}
		if( sel instanceof ItemInput){
			return ((ItemInput) sel).getItem();
		}
		return sel.getValue();
	}
	public Iterator<String> getActionNames() {
		return actions.keySet().iterator();
	}
	/** get the default action name.
	 * 
	 * @return String
	 */
	public String getSingleActionName(){
		Set<String> names = actions.keySet();
		if( names.size() == 1){
			return names.iterator().next();
		}
		return null;
	}
	public boolean hasActions(){
		return ! actions.isEmpty();
	}
	public int fieldCount(){
		return fields.size();
	}
	public void setConfirm(String name, String conf){
		FormAction action = actions.get(name);
		if( action != null){
			action.setConfirm(conf);
		}
	}
    public String getHelp(String name){
    	return tooltips.get(name);
    }
    public String getShortcut(String name){
    	return shortcuts.get(name);
    }
	/**
	 * get the contents of a form as a Map
	 * 
	 * @return Map
	 */
	public Map<String,Object> getContents() {
		Map<String,Object> m = new HashMap<String,Object>();
		update(m);
		return m;
	}

	/**
	 * get the AppContext associated with this Form
	 * 
	 * @return AppContext
	 */
	public AppContext getContext() {
		return conn;
	}

	/**
	 * get a Field object
	 * 
	 * @param key
	 * @return Field
	 */
	public Field getField(String key) {
		return  fields.get(key);
	}

	/**
	 * get an iterator over the field names
	 * 
	 * @return Iterator
	 */
	public Iterator<String> getFieldIterator() {
		return fields.keySet().iterator();
	}
	public Set<String> getFieldNames(){
		return fields.keySet();
	}

	/**
	 * get the Input associated with a Field
	 * 
	 * @param key
	 * @return the Input or null if Input does not exist
	 */
	public Input getInput(String key) {
		Field f = getField(key);
		if (f == null) {
			return null;
		}
		return f.getInput();
	}
    public Logger getLogger(){
    	return conn.getService(LoggerService.class).getLogger(getClass());
    }
    /** get the form contents as a Table
     * 
     * @return TAble
     */
    @SuppressWarnings("unchecked")
	public Table<String,String> getTable(){
    	Table t = new Table();
    	for (Iterator<String> it = getFieldIterator(); it.hasNext();) {
			String key = it.next();
			Field f = getField(key);
			Input i = f.getInput();
			Object v = i.getValue();
			if( v != null ){
				t.put("Value",f.getLabel(),i.getPrettyString(v));
			}
    	}
    	t.setKeyName("Property");
    	return t;
    }
	/**
	 * get the current Form validator
	 * 
	 * @return the FormValidator
	 */
	public final  Set<FormValidator> getValidators() {
		return new HashSet<FormValidator>(validators);
	}

	/**
	 * make a field appropriate for this form, this in principle  could be sub-classed if we
	 * want custom Fields, but it is usually simpler to implement all customisations in the Form sub-class.
	 * 
	 * @param key
	 * @param label
	 * @param sel
	 * @return Field
	 */
	protected <I> Field<I> makeField(String key, String label, Input<I> sel) {
		return  new Field<I>(key, label, sel);
	}

	/**
	 * Test if this action should be confirmed before execute
	 * 
	 * @param name
	 * @return String message name or null
	 * @throws ActionException
	 */
	public String mustConfirm(String name) throws ActionException {
		FormAction action = actions.get(name);
		if (action == null) {
			throw new ActionException("Unknown action");
		}
		return action.getConfirm(this);
	}

	/**
	 * set the value associated with a form field
	 * 
	 * @param key
	 * @param value
	 * @return previous value
	 */
	public Object put(String key, Object value) {
		Field<?> f = getField(key);
		if (f == null) {
			throw new UnsupportedOperationException("Invalid field specified "
					+ key + ":" + value);
		}
		return f.setValue(value);
	}

	/**
	 * remove a field from the form
	 * 
	 * @param key
	 */
	public void removeField(Object key) {
		fields.remove(key);
	}

	
	/**
	 * set the contents of a Form from a Map
	 * 
	 * @param m
	 *            Map of values
	 */
	public void setContents(Map<String,Object> m) {
		// only insert valid keys
		for (Iterator<String> it = getFieldIterator(); it.hasNext();) {
			String key = it.next();

			if (m.containsKey(key) ) {
				put(key, m.get(key));
			}

		}
	}

	
	/**
	 * Set a FormValidator to perform overall sanity check on the form.
	 * 
	 * @param v
	 *            The FormValidator to set.
	 * @return The previous FormValidator
	 */
	public final void addValidator(FormValidator v) {
		validators.add(v);
	}

	public final void removeValidator(FormValidator v) {
		validators.remove(v);
	}
	/**
	 * modify an existing Map updating the fields represented by the Form.
	 * Fields that are null are removed from the Map
	 * 
	 * @param m
	 *            Map to update
	 */
	public void update(Map<String,Object> m) {
		for (Iterator<String> it = getFieldIterator(); it.hasNext();) {
			String key = it.next();
			Object dat = get(key);
			if (dat != null) {
				m.put(key, dat);
			} else {
				m.remove(key);
			}
		}
	}
	/** Compare the values of this forms inputs with the contents of a Map
	 *  and generate a text summary;
	 * @param m Map to compare to
	 * @return String summary or null
	 */
    @SuppressWarnings("unchecked")
	public String diff(Map<String,Object> m){
    	StringBuilder sb = new StringBuilder();
    	for(Iterator<String> it = getFieldIterator(); it.hasNext();){
    		String key=it.next();
    	    Field f = getField(key);
    		Input i = f.getInput();
    		String label = f.getLabel();
    		Object val = i.convert(m.get(key));
    		
    		Object my_val = i.getValue();
    		String my_text="null";
    		if( my_val != null ){
    			my_text = i.getPrettyString(my_val);
    		}
    		if( val == null){
    			if(my_val != null ){
    				if( my_text.contains("\n")){
    					sb.append(label);
    					sb.append(": updated\n");
    				}else{
    					sb.append(label);
    					sb.append(": =");
    					sb.append(my_text);
    					sb.append("\n");
    				}
    			}
    		}else{
    			String text= i.getPrettyString(val);
    			if( ! val.equals(my_val) ){
    				if( val.equals("") && my_val == null ){
    					// acceptable substitution
    				}else{
    				if( text.contains("\n") || my_text.contains("\n")){
    					sb.append(label);
    					sb.append(": updated\n");
    				}else{
    					sb.append(label);
    					sb.append(": ");
    					sb.append(text);
    					sb.append("->");
    					sb.append(my_text);
    					sb.append("\n");
    				}
    				}
    			}
    		}
    	}
    	if( sb.length() > 0){
    		return sb.toString();
    	}
    	return null;
    }
	/**
	 * validate a form
	 * 
	 * @return boolean true if valid
	 * @throws ValidateException
	 */
	public final boolean validate()  {
		//int missing = 0;
				//int errors = 0;
				boolean ok = true;
				for (Iterator<String> it = getFieldIterator(); it.hasNext();) {
					String key = it.next();
					Field f = getField(key);
					try {
						f.validate();
					} catch (MissingFieldException e) {
						
						ok = false;
					} catch (FieldException e) {
						
						ok = false;
						//errors++;
					}
				}
				if (!ok) {
					return false;
				}
				for(FormValidator v : validators){
					try{
						v.validate(this);
					}catch(ValidateException e){
							return false;
					}
				}
				return true;
	}

	

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<Field> iterator() {
		return fields.values().iterator();
	}

	@Override
	public String toString() {
		return "BaseForm [fields=" + fields + ", actions=" + actions
				+ ", validators=" + validators + "]";
	}
}