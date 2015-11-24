// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data.forms.inputs;

import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
import uk.ac.ed.epcc.webapp.model.data.BasicType;

@uk.ac.ed.epcc.webapp.Version("$Id: BasicTypeInput.java,v 1.4 2015/10/13 21:40:41 spb Exp $")



public class BasicTypeInput<T extends BasicType.Value> extends TypeProducerInput<T>  {

	public BasicTypeInput(BasicType<T> t) {
		super(t); 
	}

	@Override
	public T getItembyValue(String value) {
		try {
			// Try more relaxed conversion
			return ((BasicType<T>)getProducer()).parse(value);
		} catch (ParseException e) {
			return super.getItembyValue(value);
		}
	}

	

}