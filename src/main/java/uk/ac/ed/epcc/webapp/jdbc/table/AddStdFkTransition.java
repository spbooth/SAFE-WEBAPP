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
import uk.ac.ed.epcc.webapp.forms.action.FormAction;
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.Repository;

public class AddStdFkTransition<T extends DataObjectFactory> extends EditTableFormTransition<T>{
	static final String FIELD_FORMFIELD = "Field";
	public AddStdFkTransition() {
	}

	

	@Override
	public void buildForm(Form f, T target, AppContext conn) throws TransitionException {
		TableSpecification spec = target.getTableSpecification();
		if( spec != null) {
			// use spec augemented by config
			String prefix ="create_table."+target.getTag()+".";
	    	spec.setFromParameters(conn,prefix, conn.getInitParameters(prefix));
			Repository res = getRepository(target);
			f.addInput(FIELD_FORMFIELD, "Foreign key to add", new MissingFkInput(res, spec));
			f.addAction("Add", new FormAction() {
				
				@Override
				public FormResult action(Form f) throws ActionException {
					try {
						ReferenceFieldType field = (ReferenceFieldType) f.getItem(FIELD_FORMFIELD);
						String name = (String) f.get(FIELD_FORMFIELD);
						DataBaseHandlerService dbh = res.getContext().getService(DataBaseHandlerService.class);
						dbh.addFk(res, name, field);
						return new ViewTableResult(target);
					}catch(Exception e) {
						throw new ActionException("Update failed",e);
					}
				}
			});
		}
		
	}

	
	
}