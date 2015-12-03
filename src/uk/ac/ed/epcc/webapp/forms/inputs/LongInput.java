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




public class LongInput extends NumberInput<Long> {

	public LongInput() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.epcc.webapp.model.data.forms.AbstractInput#getString()
	 */
	@Override
	public String getString(Long l) {
		if( l == null){
			return null;
		}
		if (nf == null) {
			return super.getString(l);
		}
		return nf.format(l.longValue());
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
			Long i;
			if (nf != null) {
				i = new Long(nf.parse(v.trim()).longValue());
			} else {
				i = new Long(Long.parseLong(v.trim()));
			}
			setValue(i);
		} catch (NumberFormatException e) {
			throw new ParseException("Invalid integer format");
		} catch (java.text.ParseException e) {
			throw new ParseException("Invalid integer format");
		}

	}
	@Override
	public Long convert(Object v) throws TypeError {
		if( v == null || v instanceof Long){
			return (Long) v;
		}
		if( v instanceof Number ){
			return new Long(((Number)v).longValue());
		}
		if( v instanceof String){
			return new Long((String)v);
		}
		throw new TypeError("Invalid type passed to LongInput");
	}

	@Override
	public Number getStep() {
		return 1L;
	}
}