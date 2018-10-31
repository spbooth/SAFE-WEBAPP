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



public class CommandLineForm extends MapForm {

	public CommandLineForm(AppContext c) {
		super(c);
	}

	
	public String showTable(Collection<String> missing_fields, Map<String,String> errors){
		Table<String,String> t = new Table<>();
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
	
		Map<String, String> errors= new HashMap<>();
		Set<String> missing_fields=new HashSet<>();
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
 