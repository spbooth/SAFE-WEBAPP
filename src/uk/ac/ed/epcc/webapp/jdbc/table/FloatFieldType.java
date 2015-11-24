// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.table;
@uk.ac.ed.epcc.webapp.Version("$Id: FloatFieldType.java,v 1.2 2014/09/15 14:30:26 spb Exp $")


public class FloatFieldType extends NumberFieldType<Float> {

	public FloatFieldType(boolean can_null,
			Float default_val) {
		super(Float.class, can_null, default_val);
	}

}