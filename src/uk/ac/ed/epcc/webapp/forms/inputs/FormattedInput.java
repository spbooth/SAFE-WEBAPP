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

import java.text.Format;

import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
/** Input based on a java.text.Format object
 * 
 * @author spb
 *
 * @param <N>
 */


public class FormattedInput<N> extends ParseAbstractInput<N> {
    private Format format;
    public FormattedInput(Format f){
    	format=f;
    }
    
	@SuppressWarnings("unchecked")
	public void parse(String v) throws ParseException {
		try{
		setValue((N) format.parseObject(v));
		}catch(java.text.ParseException e){
			throw new ParseException(e);
		}
	}

	@Override
	public String getString(N val) {
		return format.format(val);
	}

}