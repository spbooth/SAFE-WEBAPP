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

import java.util.StringTokenizer;

import uk.ac.ed.epcc.webapp.forms.FieldValidator;
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.model.data.forms.Selector;

/** Input for a time duration in  HH:mm::ss format
 * returned in seconds
 * 
 * @author spb
 *
 */


public class ElapsedSecondInput extends ParseAbstractInput<Number> implements FormatHintInput{

	public ElapsedSecondInput() {
		super();
		setBoxWidth(10);
		setMaxResultLength(16);
		setSingle(true);
		addValidator(new FieldValidator<Number>() {
			
			@Override
			public void validate(Number val) throws FieldException {
				if( val != null && val.intValue() < 0){
					throw new ValidateException("-ve duration");
				}
				
			}
		});
	}

	@Override
	public Long parseValue(String v) throws ParseException {
		if( v == null || v.trim().length()== 0){
			return null;
		}
		long result=0L;
		try{
		StringTokenizer st = new StringTokenizer(v,":");
		if( st.countTokens() > 3){
			throw new ParseException("Too many fields");
		}
		while( st.hasMoreElements()){
			result *= 60L;
			long val = Long.parseLong(st.nextToken());
			if( val < 0L ){
				throw new ParseException("-ve time value");
			}
			result += val;
		}
		}catch(Exception e){
			throw new ParseException("Invalid format");
		}
		return new Long(result);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.forms.AbstractInput#getString(java.lang.Object)
	 */
	@Override
	public String getString(Number val) {
		if( val == null ){
			return "";
		}
		int total=val.intValue();
		int seconds = total%60;
		total = total/60;
		int min = total%60;
		total = total/60;
		return total+":"+min+":"+seconds;
	}

	@Override
	public String getFormatHint() {
		return "HH:MM:SS";
	}

}