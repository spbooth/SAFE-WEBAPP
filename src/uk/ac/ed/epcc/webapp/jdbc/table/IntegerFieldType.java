// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.table;
@uk.ac.ed.epcc.webapp.Version("$Id: IntegerFieldType.java,v 1.2 2014/09/15 14:30:26 spb Exp $")


public class IntegerFieldType extends NumberFieldType<Integer> {

	public IntegerFieldType( boolean can_null,
			Integer default_val) {
		super(Integer.class, can_null, default_val);
	}
	public IntegerFieldType(){
		this(true,null);
	}


}