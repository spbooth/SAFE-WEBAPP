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




public class DoubleInput extends NumberInput<Double> {

	@Override
	public Double convert(Object v) throws TypeException {
		if( v == null || v instanceof Double){
			return  (Double) v;
		}
		if( v instanceof Number ){
			return  new Double(((Number)v).doubleValue());
		}
		if( v instanceof String){
			try {
				return  parseValue((String) v);
			} catch (ParseException e) {
				throw new TypeException(e);
			}
		}
		throw new TypeException(v.getClass());
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

	@Override
	public Double parseValue(String v) throws ParseException {
		if (v == null) {
			return null;
		}
		if (v.trim().length() == 0) {
			return null;
		}
		try {
			Double i;
			if (nf != null) {
				i = normalise(nf.parse(v.trim()).doubleValue());
			} else {
				i = new Double(Double.parseDouble(v.trim()));
			}
			return i;
		} catch (NumberFormatException e) {
			throw new ParseException("Invalid number format");
		} catch (java.text.ParseException e) {
			throw new ParseException("Invalid input format");
		}

	}
	/** Extension point to normalise a value after a parse
	 * 
	 * @param val
	 * @return
	 */
	protected Double normalise(Double val) {
		return val;
	}
	
	public Double setDouble(Double d) {
		try {
			return setValue(d);
		} catch (TypeException e) {
			throw new TypeError(e);
		}
	}
}