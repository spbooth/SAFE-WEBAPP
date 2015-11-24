// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.inputs;

import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;

@uk.ac.ed.epcc.webapp.Version("$Id: IntegerInput.java,v 1.3 2014/09/15 14:30:19 spb Exp $")



public class IntegerInput extends NumberInput<Integer> {

	public IntegerInput() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.epcc.webapp.model.data.forms.AbstractInput#getString()
	 */
	
	
	@Override
	public String getString(Integer i) {
		if (nf == null) {
			return super.getString(i);
		}
		return nf.format(i.intValue());
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
			Integer i;
			if (nf != null) {
				i = new Integer(nf.parse(v.trim()).intValue());
			} else {
				i = new Integer(Integer.parseInt(v.trim()));
			}
			setValue(i);
		} catch (NumberFormatException e) {
			throw new ParseException("Invalid integer format");
		} catch (java.text.ParseException e) {
			throw new ParseException("Invalid integer format");
		}

	}
	@Override
	public Integer convert(Object v) throws TypeError {
		if( v == null || v instanceof Integer){
			return (Integer) v;
		}
		if( v instanceof Number ){
			return new Integer(((Number)v).intValue());
		}
		if( v instanceof String){
			return new Integer((String)v);
		}
		throw new TypeError("Invalid type "+v.getClass().getCanonicalName()+" passed to IntegerInput");
	}

	@Override
	public Number getStep() {
		return 1;
	}
}