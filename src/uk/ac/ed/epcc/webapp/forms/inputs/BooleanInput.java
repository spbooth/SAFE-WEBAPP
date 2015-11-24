// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.inputs;

import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;

@uk.ac.ed.epcc.webapp.Version("$Id: BooleanInput.java,v 1.2 2014/09/15 14:30:18 spb Exp $")



public class BooleanInput implements ParseInput<Boolean> ,BinaryInput<Boolean>{
    private Boolean value=Boolean.FALSE;
    private String key;
	public String getString() {
		return value.toString();
	}

	public void parse(String v) throws ParseException {
		if( v==null || v.trim().length() == 0){
			// unchecked boxes are false
			value = Boolean.FALSE;
		}else{
			value = Boolean.valueOf(v);
		}
	}

	public Boolean convert(Object v) throws TypeError {
		if( v instanceof String ){
		    return Boolean.valueOf((String) v);
		}
		if( v instanceof Boolean){
			return (Boolean) v;
		}
		return Boolean.FALSE;
	}

	public String getKey() {
		return key;
	}

	public String getPrettyString(Boolean value) {
		return value.toString();
	}

	public String getString(Boolean value) {
		return value.toString();
	}

	public Boolean getValue() {
		return value;
	}

	public void setKey(String key) {
		this.key=key;
	}

	public Boolean setValue(Boolean v) throws TypeError {
		if( v == null ){
			v = Boolean.FALSE;
		}
		Boolean old = value;
		value=v;
		return old;
	}

	public void validate() throws FieldException {
		return;
	}

	public final <R> R accept(InputVisitor<R> vis) throws Exception {
		return vis.visitBinaryInput(this);
	}

	public boolean isChecked() {
		return value;
	}

	public void setChecked(boolean value) {
		setValue(value);
	}

	public String getChecked() {
		return "true";
	}

}