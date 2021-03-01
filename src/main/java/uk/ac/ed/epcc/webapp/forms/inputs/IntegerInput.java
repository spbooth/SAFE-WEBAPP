//| Copyright - The University of Edinburgh 2011                            |
//|                                                                         |
//| Licensed under the Apache License, Version 2.0 (the "License");         |
//| you may not use this file except in compliance with the License.        |
//| You may obtain a copy of the License at                                 |
//|                                                                         |
//|    http://www.apache.org/licenses/LICENSE-2.0                           |
//|                                                                         |
//| Unless required by applicable law or agreed to in writing, software     |
//| distributed under the License is distributed on an "AS IS" BASIS,       |
//| WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.|
//| See the License for the specific language governing permissions and     |
//| limitations under the License.                                          |
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.inputs;

import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;





public class IntegerInput extends NumberInput<Integer> {

	public IntegerInput() {
		super();
		setStep(1);
	}

	public Integer setInteger(int i) {
		try {
			return setValue(i);
		} catch (TypeException e) {
			throw new TypeError(e);
		}
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

	@Override
	public Integer parseValue(String v) throws ParseException {
		if (v == null) {
			return null;
		}
		if (v.trim().length() == 0) {
			return null;
		}
		try {
			Integer i;
			if (nf != null) {
				i = new Integer(nf.parse(v.trim()).intValue());
			} else {
				i = new Integer(Integer.parseInt(v.trim()));
			}
			return i;
		} catch (NumberFormatException e) {
			throw new ParseException("Invalid integer format");
		} catch (java.text.ParseException e) {
			throw new ParseException("Invalid integer format");
		}

	}
	@Override
	public Integer convert(Object v) throws TypeException {
		if( v == null || v instanceof Integer){
			return (Integer) v;
		}
		if( v instanceof Number ){
			return new Integer(((Number)v).intValue());
		}
		if( v instanceof String){
			return new Integer((String)v);
		}
		throw new TypeException("Invalid type "+v.getClass().getCanonicalName()+" passed to IntegerInput");
	}
}