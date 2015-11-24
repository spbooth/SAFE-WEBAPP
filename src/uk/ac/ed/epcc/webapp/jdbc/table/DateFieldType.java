// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.table;

import java.util.Date;
@uk.ac.ed.epcc.webapp.Version("$Id: DateFieldType.java,v 1.3 2014/12/28 13:12:42 spb Exp $")

/** Default field type for storing java {@link Date} objects.
 * 
 * @author spb
 *
 */
public class DateFieldType extends FieldType<Date> {

	public DateFieldType( boolean can_null,
			Date default_val) {
		super(Date.class, can_null, default_val);
	}

	@Override
	public void accept(FieldTypeVisitor vis) {
		vis.visitDateFieldType(this);
	}

}