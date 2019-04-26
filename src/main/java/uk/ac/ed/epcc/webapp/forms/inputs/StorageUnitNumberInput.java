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

import uk.ac.ed.epcc.webapp.Units;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;

/** An {@link Input} for storage size in specified {@link Units}.
 * The returned value (and the bounds etc.) are always in bytes but the text representation 
 * (and numeric values passed to {@link #convert(Object)} are in the specified unit.
 * @author Stephen Booth
 *
 */
public class StorageUnitNumberInput extends NumberInput<Long> {

	private final Units unit;
	/**
	 * 
	 */
	public StorageUnitNumberInput(Units unit) {
		this.unit=unit;
		setUnit(unit.toString());
		setStep(unit.bytes);
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.ParseInput#parse(java.lang.String)
	 */
	@Override
	public Long parseValue(String v) throws ParseException {
		if( v == null || v.trim().isEmpty()) {
			return null;
		}
		try {
			Long l = Long.parseLong(v);
			 return (unit.bytes * l.longValue());
		}catch(Exception e) {
			throw new ParseException(e);
		}
	}
	@Override
	public String getString(Long val) {
		return Long.toString(val.longValue()/unit.bytes);
	}
	@Override
	public Long convert(Object v) throws TypeError {
		if( v == null ) {
			return null;
		}
		if( v instanceof Number) {
			return ((Number)v).longValue();
		}
		if( v instanceof String ) {
			try {
				return parseValue((String) v);
			} catch (ParseException e) {
				throw new TypeError(e);
			}
		}
		throw new TypeError(v.getClass());
	}
	@Override
	public String getPrettyString(Long val) {
		if( val == null) {
			return super.getPrettyString(val);
		}
		return getString(val)+" "+unit.toString();
	}
	

}
