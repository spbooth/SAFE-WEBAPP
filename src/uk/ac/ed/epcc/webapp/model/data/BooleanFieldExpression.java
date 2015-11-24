// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data;

import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
@uk.ac.ed.epcc.webapp.Version("$Id: BooleanFieldExpression.java,v 1.14 2014/09/15 14:30:28 spb Exp $")


public class BooleanFieldExpression<X extends DataObject> extends FieldExpression<Boolean,X> {
	
	protected BooleanFieldExpression(Class<? super X> filter_type,Repository repository,String field) {
		super(filter_type,repository, Boolean.class, field);
	}

	protected void setValue(Record r, Boolean value) {
		r.setProperty(name, value);
	}

	protected Boolean getValue(Record r) {
		return r.getBooleanProperty(name);
	}



}