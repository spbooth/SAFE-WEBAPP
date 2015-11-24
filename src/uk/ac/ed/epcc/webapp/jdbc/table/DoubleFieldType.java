// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.table;
@uk.ac.ed.epcc.webapp.Version("$Id: DoubleFieldType.java,v 1.2 2014/09/15 14:30:26 spb Exp $")


public class DoubleFieldType extends NumberFieldType<Double> {

	public DoubleFieldType(boolean can_null,
			Double default_val) {
		super(Double.class, can_null, default_val);
	}

}