// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.table;
@uk.ac.ed.epcc.webapp.Version("$Id: StringFieldType.java,v 1.2 2014/09/15 14:30:26 spb Exp $")


public class StringFieldType extends FieldType<String> {
    private final int max_length;
	public StringFieldType( boolean can_null,
			String default_val, int max_length) {
		super(String.class, can_null, default_val);
		this.max_length=max_length;
	}
	public int getMaxLEngth(){
		return max_length;
	}
	@Override
	public void accept(FieldTypeVisitor vis) {
		vis.visitStringFieldType(this);
	}

}