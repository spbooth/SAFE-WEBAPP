// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.inputs;

import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;

@uk.ac.ed.epcc.webapp.Version("$Id: DoubleInput.java,v 1.3 2014/09/15 14:30:19 spb Exp $")


public class DoubleInput extends NumberInput<Double> {

	@Override
	public Double convert(Object v) throws TypeError {
		if( v == null || v instanceof Double){
			return  (Double) v;
		}
		if( v instanceof Number ){
			return  new Double(((Number)v).doubleValue());
		}
		if( v instanceof String){
			return  new Double((String)v);
		}
		throw new TypeError(v.getClass());
	}

	public DoubleInput() {
		super();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.epcc.webapp.model.data.forms.AbstractInput#getString()
	 */
	@Override
	public String getString(Double val) {
		if (nf == null) {
			return super.getString(val);
		}
		return nf.format(val.doubleValue());
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
			Double i;
			if (nf != null) {
				i = nf.parse(v.trim()).doubleValue();
			} else {
				i = new Double(Double.parseDouble(v.trim()));
			}
			setValue( i);
		} catch (NumberFormatException e) {
			throw new ParseException("Invalid number format");
		} catch (java.text.ParseException e) {
			throw new ParseException("Invalid input format");
		}

	}
	
}