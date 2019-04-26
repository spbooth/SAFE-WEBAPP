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

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.FieldValidator;
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.forms.inputs.NoSpaceFieldValidator;
import uk.ac.ed.epcc.webapp.forms.inputs.TextInput;
import uk.ac.ed.epcc.webapp.jdbc.table.DataBaseHandlerService;
/** Input to generate a name for a new database table.
 * 
 * @author spb
 *
 */


public class NewTableInput extends TextInput {
	/**
	 * @author Stephen Booth
	 *
	 */
	public final class NewTableNameValidator implements FieldValidator<String> {
		/**
		 * 
		 */
		private final AppContext c;

		/**
		 * @param c
		 */
		public NewTableNameValidator(AppContext c) {
			this.c = c;
		}

		@Override
		public void validate(String data) throws FieldException {
			DataBaseHandlerService serv = c.getService(DataBaseHandlerService.class);
			if( serv != null ){
				if( serv.tableExists(data)){
					throw new ValidateException("Table "+getValue()+" already exists");
				}
			}
			
		}
	}
	public NewTableInput(AppContext c){
		setSingle(true);
		setTrim(true);
		addValidator(new NoSpaceFieldValidator());
		addValidator(new NewTableNameValidator(c));
	}
	
}