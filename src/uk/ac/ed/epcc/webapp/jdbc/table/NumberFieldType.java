// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.table;
@uk.ac.ed.epcc.webapp.Version("$Id: NumberFieldType.java,v 1.2 2014/09/15 14:30:26 spb Exp $")


public  class NumberFieldType<N extends Number> extends FieldType<N> {

	public NumberFieldType(Class<? super N> clazz, boolean can_null,
			N default_val) {
		super(clazz, can_null, default_val);
	}

	@Override
	public void accept(FieldTypeVisitor vis) {
		vis.visitNumberFieldType(this);
		
	}

}