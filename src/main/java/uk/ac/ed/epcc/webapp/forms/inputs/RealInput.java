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

	@Override
	public Float parseValue(String v) throws ParseException {
		if (v == null) {
			return null;
		}
		if (v.trim().length() == 0) {
			return null;
		}
		try {
			Float i;
			if (nf != null) {
				i = new Float(nf.parse(v.trim()).floatValue());
			} else {
				i = new Float(Float.parseFloat(v.trim()));
			}
			return i;
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