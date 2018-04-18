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
package uk.ac.ed.epcc.webapp.jdbc.table;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.forms.inputs.TableInput;



public class AddReferenceTransition<T extends DataObjectFactory> extends AddFieldTransition<T> {
	private static final String TABLE = "Table";
	public AddReferenceTransition(){
		super();
	}
	
	@Override
	protected void addFormParams(Form f, AppContext c) {
		f.addInput(TABLE, "Table to reference", new TableInput<DataObjectFactory>(c,DataObjectFactory.class ));
	}
	@Override
	protected FieldType getFieldType(Form f) {
		return new ReferenceFieldType((String) f.get(TABLE));
	}

}