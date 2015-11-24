// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.table;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.Repository;
import uk.ac.ed.epcc.webapp.model.data.forms.inputs.TableInput;
@uk.ac.ed.epcc.webapp.Version("$Id: AddReferenceTransition.java,v 1.2 2014/09/15 14:30:25 spb Exp $")


public class AddReferenceTransition<T extends TableStructureTransitionTarget> extends AddFieldTransition<T> {
	private static final String TABLE = "Table";
	public AddReferenceTransition(Repository res){
		super(res);
	}
	
	@Override
	protected void addFormParams(Form f, AppContext c) {
		f.addInput(TABLE, "Table to reference", new TableInput<DataObjectFactory>(c,DataObjectFactory.class ));
	}
	@Override
	protected FieldType getFieldType(Form f) {
		return new ReferenceFieldType((String) f.get(TABLE));
	}

}