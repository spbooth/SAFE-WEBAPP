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

import uk.ac.ed.epcc.webapp.forms.FieldValidator;
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.forms.inputs.TextInput;
import uk.ac.ed.epcc.webapp.model.data.Repository;
/** Input to select a name that is not one of the current fields of a Repository
 * 
 * @author spb
 *
 */


public class NewFieldInput extends TextInput {
	/**
	 * @author Stephen Booth
	 *
	 */
	public final class NewFieldValidator implements FieldValidator<String> {
		/**
		 * 
		 */
		private final Repository res;

		/**
		 * @param res
		 */
		public NewFieldValidator(Repository res) {
			this.res = res;
		}

		@Override
		public void validate(String data) throws FieldException {
			if( res.getInfo(data) != null ){
				throw new ValidateException("Name already in use");
			}
			
		}
	}
	
	public NewFieldInput(Repository res) {
		super();
		setBoxWidth(32);
		setSingle(true);
		addValidator(new NewFieldValidator(res));
	}
	

}