// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.table;
@uk.ac.ed.epcc.webapp.Version("$Id: LongFieldType.java,v 1.2 2014/09/15 14:30:26 spb Exp $")


public class LongFieldType extends NumberFieldType<Long> {

	public LongFieldType( boolean can_null,
			Long default_val) {
		super(Long.class, can_null, default_val);
	}

}