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
import java.util.Date;


/** A text input for {@link Date}s on day boundaries-
 * 
 * @author spb
 *
 */
public class DateInput extends AbstractDateInput implements HTML5Input, PatternInput {
    public static final String DEFAULT_FORMAT = "yyyy-MM-dd";

    public DateInput(){
    	super();
    }
	public DateInput(long resolution) {
		super(resolution);
		
	}
	

	@Override
	public String[] getFormats(){
		return new String[] {DEFAULT_FORMAT} ;
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.HTML5Input#getType()
	 */
	@Override
	public String getType() {
		return "date";
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.PatternInput#getPattern()
	 */
	@Override
	public String getPattern() {
		return "\\d{4}-[0-1]\\d-[0-3]\\d";
	}
	
}