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
package uk.ac.ed.epcc.webapp.model.data.forms.inputs;

import java.util.StringTokenizer;

import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.forms.inputs.ElapsedSecondInput;
import uk.ac.ed.epcc.webapp.forms.inputs.ParseAbstractInput;
import uk.ac.ed.epcc.webapp.forms.inputs.TagInput;
import uk.ac.ed.epcc.webapp.forms.inputs.TypeError;
import uk.ac.ed.epcc.webapp.model.data.Duration;
/** Input for a {@link Duration} in  HH:mm::ss format.
 *  @see ElapsedSecondInput
 * 
 * @author spb
 *
 */


public class DurationInput extends ParseAbstractInput<Duration> implements TagInput{

	public DurationInput() {
		super();
		setBoxWidth(10);
		setMaxResultLength(16);
		setSingle(true);
	}

	public void parse(String v) throws ParseException {
		if( v == null || v.trim().length()== 0){
			setValue(null);
			return;
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
		setValue(new Duration(result));
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.forms.AbstractInput#getString(java.lang.Object)
	 */
	@Override
	public String getString(Duration val) {
		if( val == null ){
			return "";
		}
		long total=val.getSeconds();
		long seconds = total%60L;
		total = total/60L;
		long min = total%60L;
		total = total/60L;
		return total+":"+min+":"+seconds;
	}

	public String getTag() {
		return "(HH:MM:SS)";
	}

	@Override
	public void validate() throws FieldException {
		super.validate();
		Duration val = getValue();
		if( val != null && val.getSeconds() < 0L){
			throw new ValidateException("-ve duration");
		}
	}

	@Override
	public Duration convert(Object v) throws TypeError {
		if( v == null ){
			return null;
		}
		if( v instanceof Duration){
			return (Duration)v;
		}
		if( v instanceof Number){
			return new Duration((Number)v);
		}
		throw new TypeError(v.getClass());
	}

}