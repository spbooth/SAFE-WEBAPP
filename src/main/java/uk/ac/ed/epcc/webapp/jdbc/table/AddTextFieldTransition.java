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
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.table;



import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.inputs.BooleanInput;
import uk.ac.ed.epcc.webapp.forms.inputs.IntegerInput;
import uk.ac.ed.epcc.webapp.forms.inputs.TextInput;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;



public class AddTextFieldTransition<T extends DataObjectFactory> extends AddFieldTransition<T> {

	private static final String DEFAULT = "Default";
	private static final String SIZE = "Size";
	private static final String ALLOW_NULL ="AllowNull";

	public AddTextFieldTransition() {
		super();
	}

	@Override
	protected void addFormParams(Form f, T target,AppContext c) {
		IntegerInput length = new IntegerInput();
		BooleanInput null_input = new BooleanInput();
		length.setMin(1);
		TextInput def = new TextInput();
		f.addInput(SIZE,"Max length", length);
		f.addInput(ALLOW_NULL, "Allow null", null_input);
		f.addInput(DEFAULT,"Default value",def).setOptional(true);
		
	}

	@Override
	protected FieldType getFieldType(Form f) {
		IntegerInput length = (IntegerInput) f.getInput(SIZE);
		Boolean allowNull = (Boolean) f.get(ALLOW_NULL);
		String default_value = (String) f.get(DEFAULT);
		if( ! allowNull && default_value == null){
			default_value="";
		}
		return new StringFieldType(allowNull, default_value, length.getValue());
	}

	
}