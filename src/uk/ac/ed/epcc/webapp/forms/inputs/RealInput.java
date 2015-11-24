// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.inputs;

import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;

@uk.ac.ed.epcc.webapp.Version("$Id: RealInput.java,v 1.2 2014/09/15 14:30:20 spb Exp $")


public class RealInput extends NumberInput<Float> {

	public RealInput() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.epcc.webapp.model.data.forms.AbstractInput#getString()
	 */
	@Override
	public String getString(Float f) {
		if (nf == null) {
			return super.getString(f);
		}
		return nf.format(f.floatValue());
	}

	public void parse(String v) throws ParseException {
		if (v == null) {
			setValue(null);
			return;
		}
		if (v.trim().length() == 0) {
			setValue(null);
			return;
		}
		try {
			Float i;
			if (nf != null) {
				i = new Float(nf.parse(v.trim()).floatValue());
			} else {
				i = new Float(Float.parseFloat(v.trim()));
			}
			setValue(i);
		} catch (NumberFormatException e) {
			throw new ParseException("Invalid real format");
		} catch (java.text.ParseException e) {
			throw new ParseException("Invalid integer format");
		}

	}
	@Override
	public Float convert(Object v) throws TypeError {
		if( v == null || v instanceof Float){
			return (Float) v;
		}
		if( v instanceof Number ){
			return new Float(((Number)v).floatValue());
		}
		if( v instanceof String){
			return new Float((String)v);
		}
		throw new TypeError(v.getClass());
	}
}