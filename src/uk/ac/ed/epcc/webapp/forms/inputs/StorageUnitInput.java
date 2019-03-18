//| Copyright - The University of Edinburgh 2019                            |
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
package uk.ac.ed.epcc.webapp.forms.inputs;

import uk.ac.ed.epcc.webapp.UnitFormat;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;

/**
 * @author Stephen Booth
 *
 */
public class StorageUnitInput extends NumberInput<Long> {

	/**
	 * 
	 */
	public StorageUnitInput() {
		setMin(1L);
		setNumberFormat(new UnitFormat());
	}

	

	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.ParseInput#parse(java.lang.String)
	 */
	@Override
	public void parse(String v) throws ParseException {
		if( v == null || v.isEmpty()) {
			setValue(null);
		}else {
			try {
				setValue((Long) nf.parse(v));
			} catch (Exception e) {
				throw new ParseException(e);
			}
		}

	}




	@Override
	public Long convert(Object v) throws TypeError {
		if( v == null ) {
			return null;
		}
		if( v instanceof String) {
			try {
				return (Long) nf.parse((String)v);
			} catch (java.text.ParseException e) {
				throw new TypeError(e);
			}
		}
		if( v instanceof Number ) {
			return Long.valueOf(((Number)v).longValue());
		}
		throw new TypeError("Type "+v.getClass().getCanonicalName()+" not convertable to Long");
	}




	@Override
	public String getType() {
		// can't be a html5 number input with custom format browser may not show
		return null;
	}

}
