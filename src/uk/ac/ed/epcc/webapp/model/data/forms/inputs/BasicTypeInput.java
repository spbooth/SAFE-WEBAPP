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

import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
import uk.ac.ed.epcc.webapp.forms.inputs.NameInput;
import uk.ac.ed.epcc.webapp.model.data.BasicType;





public class BasicTypeInput<T extends BasicType.Value> extends TypeProducerInput<T>  implements NameInput<T>{

	public BasicTypeInput(BasicType<T> t) {
		super(t); 
	}

	@Override
	public T getItembyValue(String value) {
		try {
			// Try more relaxed conversion
			return ((BasicType<T>)getProducer()).parse(value);
		} catch (ParseException e) {
			return super.getItembyValue(value);
		}
	}

	

}