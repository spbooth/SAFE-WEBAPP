//| Copyright - The University of Edinburgh 2018                            |
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
package uk.ac.ed.epcc.webapp.jdbc.table;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.Repository;

public class AddStdFieldTransition<T extends DataObjectFactory> extends AddFieldTransition<T>{
	static final String FIELD_FORMFIELD = "Field";
	public AddStdFieldTransition() {
	}

	@Override
	protected void addFormParams(Form f, T target, AppContext c) {
		TableSpecification spec = target.getTableSpecification();
		if( spec != null) {
			// use spec augemented by config
			String prefix ="create_table."+target.getTag()+".";
	    	spec.setFromParameters(c,prefix, c.getInitParameters(prefix));
			Repository res = getRepository(target);
			f.addInput(FIELD_FORMFIELD, "Field to add", new OptionalFieldInput<>(res,true,  spec.getStdFields()));
		}
	}

	@Override
	protected FieldType getFieldType(Form f) {
		return (FieldType) f.getItem(FIELD_FORMFIELD);
	}
	
}