// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.table;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.inputs.BooleanInput;
import uk.ac.ed.epcc.webapp.model.data.Repository;
@uk.ac.ed.epcc.webapp.Version("$Id: AddBooleanFieldTransition.java,v 1.2 2014/09/15 14:30:25 spb Exp $")


public class AddBooleanFieldTransition<T extends TableStructureTransitionTarget> extends AddFieldTransition<T> {

	private static final String DEFAULT = "Default";
	
	public AddBooleanFieldTransition(Repository res) {
		super(res);
	}

	@Override
	protected void addFormParams(Form f, AppContext c) {
		BooleanInput input = new BooleanInput();
		f.addInput(DEFAULT, "Default true", input);
		
	}

	@Override
	protected FieldType getFieldType(Form f) {
		BooleanInput input = (BooleanInput) f.getInput(DEFAULT);
		return new BooleanFieldType(false, input.getValue());
	}

}