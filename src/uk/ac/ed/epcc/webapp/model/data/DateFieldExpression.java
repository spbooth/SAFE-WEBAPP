// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
/**
 * 
 */
package uk.ac.ed.epcc.webapp.model.data;

import java.util.Date;

import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
@uk.ac.ed.epcc.webapp.Version("$Id: DateFieldExpression.java,v 1.17 2014/09/15 14:30:28 spb Exp $")


/** A {@link FieldExpression} for a SQL date field.
 * 
 * 
 * @author spb
 *
 * @param <X>
 */
public class DateFieldExpression<X extends DataObject> extends FieldExpression<Date,X> {
	protected DateFieldExpression(Class<? super X> filter_type,Repository res,String field) {
		super(filter_type,res, Date.class,field);
	}
	protected Date getValue(Record r) {
		return r.getDateProperty(name);
	}
	protected void setValue(Record r, Date value) {
		r.setProperty(name, value);
	}
	
}