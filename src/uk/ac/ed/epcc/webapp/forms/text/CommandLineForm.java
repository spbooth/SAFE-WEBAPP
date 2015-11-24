// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp.forms.text;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.Table;
import uk.ac.ed.epcc.webapp.forms.Field;
import uk.ac.ed.epcc.webapp.forms.MapForm;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.inputs.MultiInput;
@uk.ac.ed.epcc.webapp.Version("$Id: CommandLineForm.java,v 1.3 2014/09/15 14:30:22 spb Exp $")


public class CommandLineForm extends MapForm {

	public CommandLineForm(AppContext c) {
		super(c);
	}

	
	public String showTable(Collection<String> missing_fields, Map<String,String> errors){
		Table<String,String> t = new Table<String,String>();
		for(Field f : this){
			String key = f.getKey();
			t.put("label", key, f.getLabel());
			if( missing_fields != null && missing_fields.contains(key)){
				t.put("missing", key, "*");
			}
			if( errors != null && errors.containsKey(key)){
				t.put("error",key,errors.get(key));
			}
			Input input = f.getInput();
			addInput(t, input);
			
		}
		return t.getString();
	}
	@SuppressWarnings("unchecked")
	private void addInput(Table t, Input input){
		String key = input.getKey();
		t.put("parameter",key,key);
		t.put("value", key, input.getPrettyString(input.getValue()));
		if( input instanceof MultiInput){
			MultiInput<?,?> mi = (MultiInput) input;
			for( String sub_key : mi.getSubKeys()){
				Input nest = mi.getInput(sub_key);
				addInput(t, nest);
			}
		}
	}
	public boolean parseParams(Map<String,Object> params){
	
		Map<String, String> errors= new HashMap<String,String>();
		Set<String> missing_fields=new HashSet<String>();
		boolean ok = parsePost(errors, params,true);
		if( ok ){
			ok = validate(missing_fields, errors);
		}
		if( ! ok ){
			System.err.println(showTable(missing_fields, errors));
		}
		return ok;
	}
}
 