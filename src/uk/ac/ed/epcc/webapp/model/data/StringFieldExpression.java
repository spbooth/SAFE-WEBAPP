// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
/**
 * 
 */
package uk.ac.ed.epcc.webapp.model.data;

import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
@uk.ac.ed.epcc.webapp.Version("$Id: StringFieldExpression.java,v 1.15 2014/09/15 14:30:29 spb Exp $")


public class StringFieldExpression<X extends DataObject> extends FieldExpression<String,X> {
	protected StringFieldExpression(Class<? super X> filter_type,Repository res,String field) {
		super(filter_type,res, String.class,field);
	}
	protected String getValue(Record r) {
		return r.getStringProperty(name);
	}
	protected void setValue(Record r, String value) {
		r.setProperty(name, value);
	}
}