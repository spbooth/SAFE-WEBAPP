// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.table;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.inputs.IntegerInput;
import uk.ac.ed.epcc.webapp.model.data.Repository;
@uk.ac.ed.epcc.webapp.Version("$Id: AddIntegerFieldTransition.java,v 1.2 2014/09/15 14:30:25 spb Exp $")


public class AddIntegerFieldTransition<T extends TableStructureTransitionTarget> extends AddFieldTransition<T> {

	private static final String DEFAULT = "Default";

	public AddIntegerFieldTransition(Repository res) {
		super(res);
	}

	@Override
	protected void addFormParams(Form f, AppContext c) {
		IntegerInput input = new IntegerInput();
		input.setOptional(true);
		f.addInput(DEFAULT,"Default value", input);
	}

	@Override
	protected FieldType getFieldType(Form f) {
		return new IntegerFieldType(true, (Integer) f.get(DEFAULT));
	}

	

}