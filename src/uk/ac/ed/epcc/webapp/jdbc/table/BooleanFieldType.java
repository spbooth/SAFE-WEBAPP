// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.table;
@uk.ac.ed.epcc.webapp.Version("$Id: BooleanFieldType.java,v 1.2 2014/09/15 14:30:26 spb Exp $")


public class BooleanFieldType extends FieldType<Boolean> {

	public BooleanFieldType(boolean allow_null,
			Boolean defaultVal) {
		super(Boolean.class, allow_null, defaultVal);
	}

	@Override
	public void accept(FieldTypeVisitor vis) {
		vis.visitBooleanFieldType(this);
	}

}