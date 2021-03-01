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
import uk.ac.ed.epcc.webapp.Units;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
import uk.ac.ed.epcc.webapp.model.data.forms.Selector;

/** A {@link Input} for storage sizes where the
 * unit is parsed as part of the input.
 * @author Stephen Booth
 *
 */
public class StorageUnitTextInput extends NumberInput<Long> implements FormatHintInput{

	/**
	 * 
	 */
	public StorageUnitTextInput() {
		setMin(1L);
		setNumberFormat(new UnitFormat());
	}

	

	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.ParseInput#parse(java.lang.String)
	 */
	@Override
	public Long parseValue(String v) throws ParseException {
		if( v == null || v.isEmpty()) {
			return null;
		}else {
			try {
				return Long.valueOf(nf.parse(v).longValue());
			} catch (Exception e) {
				throw new ParseException(e);
			}
		}

	}




	@Override
	public Long convert(Object v) throws TypeException {
		if( v == null ) {
			return null;
		}
		if( v instanceof String) {
			try {
				return (Long) nf.parse((String)v);
			} catch (java.text.ParseException e) {
				throw new TypeException(e);
			}
		}
		if( v instanceof Number ) {
			return Long.valueOf(((Number)v).longValue());
		}
		throw new TypeException("Type "+v.getClass().getCanonicalName()+" not convertable to Long");
	}




	@Override
	public String getType() {
		// can't be a html5 number input with custom format browser may not show
		return null;
	}




	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.FormatHintInput#getFormatHint()
	 */
	@Override
	public String getFormatHint() {

		return "500"+Units.KiB.toString();
	}
}
