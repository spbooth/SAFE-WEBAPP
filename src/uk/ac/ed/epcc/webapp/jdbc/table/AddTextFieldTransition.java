// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.table;



import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.inputs.BooleanInput;
import uk.ac.ed.epcc.webapp.forms.inputs.IntegerInput;
import uk.ac.ed.epcc.webapp.forms.inputs.TextInput;
import uk.ac.ed.epcc.webapp.model.data.Repository;
@uk.ac.ed.epcc.webapp.Version("$Id: AddTextFieldTransition.java,v 1.2 2014/09/15 14:30:25 spb Exp $")


public class AddTextFieldTransition<T extends TableStructureTransitionTarget> extends AddFieldTransition<T> {

	private static final String DEFAULT = "Default";
	private static final String SIZE = "Size";
	private static final String ALLOW_NULL ="AllowNull";

	public AddTextFieldTransition(Repository res) {
		super(res);
	}

	@Override
	protected void addFormParams(Form f, AppContext c) {
		IntegerInput length = new IntegerInput();
		BooleanInput null_input = new BooleanInput();
		length.setMin(1);
		length.setOptional(false);
		TextInput def = new TextInput(true);
		def.setOptional(true);
		f.addInput(SIZE,"Max length", length);
		f.addInput(ALLOW_NULL, "Allow null", null_input);
		f.addInput(DEFAULT,"Default value",def);
		
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