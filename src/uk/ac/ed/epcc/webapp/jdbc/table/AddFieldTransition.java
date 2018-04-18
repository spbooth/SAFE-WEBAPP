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


import java.util.LinkedList;
import java.util.List;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.config.ConfigService;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.action.FormAction;
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.jdbc.SQLContext;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.Repository;
import uk.ac.ed.epcc.webapp.model.data.forms.inputs.NewFieldInput;

public abstract class AddFieldTransition<T extends DataObjectFactory>
		extends EditTableFormTransition<T> {
	
	/**
	 * 
	 */
	static final String ADD_ACTION = "Add";
	public static final String FIELD = "Field";


	public AddFieldTransition(){
	}
	public final void buildForm(Form f, T target, AppContext c)
			throws TransitionException {
		f.addInput(FIELD, "New Field Name", new NewFieldInput(getRepository(target)));
		addFormParams(f, c);
		f.addAction(ADD_ACTION, new AddFieldAction(target));

	}

	protected abstract void addFormParams(Form f, AppContext c);

	protected abstract FieldType getFieldType(Form f);



	public class AddFieldAction extends FormAction {

		private final T target;
		public AddFieldAction(T target) {
			this.target=target;
		}

		@Override
		public FormResult action(Form f)
				throws uk.ac.ed.epcc.webapp.forms.exceptions.ActionException {
			try {
				Repository res = getRepository(target);
				SQLContext sql = res.getSQLContext();
				StringBuilder query = new StringBuilder();
				query.append("ALTER TABLE ");
				res.addTable(query, true);
				query.append(" ADD ");
				String name = (String) f.get(FIELD);
				sql.quote(query,name);
				query.append(" ");
				List<Object> args = new LinkedList<Object>();
				FieldType fieldType = getFieldType(f);
				fieldType.accept(sql.getCreateVisitor(query, args));
				java.sql.PreparedStatement stmt = sql.getConnection().prepareStatement(query.toString());
				int pos=1;
				for(Object o: args){
					stmt.setObject(pos++, o);
				}
				stmt.execute();
				stmt.close();
				if( fieldType instanceof ReferenceFieldType){
					ReferenceFieldType ref = (ReferenceFieldType) fieldType;
					ConfigService serv = res.getContext().getService(ConfigService.class);
					try{
    					serv.setProperty("reference."+res.getTag()+"."+name, ref.getRemoteTable());
    				}catch(UnsupportedOperationException e){
    					
    				}
				}
				resetStructure(target);
				
			} catch (Exception e) {
				throw new ActionException("Update failed",e);
			}
			return new ViewTableResult(target);
		}

	}
}