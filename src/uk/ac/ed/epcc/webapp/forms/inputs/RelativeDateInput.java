// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.inputs;

import java.text.DateFormat;


/** A DateInput that parses a relative date notation.
 * 
 * @see RelativeDateFormat
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: RelativeDateInput.java,v 1.3 2014/09/15 14:30:20 spb Exp $")

public class RelativeDateInput extends DateInput {

	public RelativeDateInput() {
		super();
	}

	public RelativeDateInput(long resolution) {
		super(resolution);
	}

	@Override
	protected DateFormat getDateFormat(String format) {
		return new RelativeDateFormat(format);
	}

	
}