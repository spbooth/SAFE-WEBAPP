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
import uk.ac.ed.epcc.webapp.forms.action.FormAction;
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.inputs.ItemInput;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractFormTransition;
import uk.ac.ed.epcc.webapp.jdbc.SQLContext;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.Repository;
import uk.ac.ed.epcc.webapp.model.data.Repository.FieldInfo;
import uk.ac.ed.epcc.webapp.model.data.forms.inputs.RepositoryFieldInput;



public class DropFieldTransition<T extends DataObjectFactory> extends EditTableFormTransition<T>{
    /**
	 * 
	 */
	private static final String FIELD_INPUT = "Field";
	
    public DropFieldTransition(){
    }
	public void buildForm(Form f, T target, AppContext c)
			throws TransitionException {
		f.addInput(FIELD_INPUT, "Field to drop", getFieldInput(target));
		f.addAction("Drop", new DropAction(target));
	}
	public <I extends ItemInput<String,FieldInfo>> I getFieldInput(T target) {
		return (I) new RepositoryFieldInput(getRepository(target));
	}
	public class DropAction extends FormAction{
		private T target;
		public DropAction(T target){
			super();
			setConfirm("drop_field");
			this.target=target;
		}
		@Override
		public FormResult action(Form f) throws ActionException {
			ItemInput<String,FieldInfo> input = (ItemInput<String,FieldInfo>) f.getInput(FIELD_INPUT);
			try {
				Repository res = getRepository(target);
				SQLContext sql = res.getSQLContext();
				StringBuilder query = new StringBuilder();
				query.append("ALTER TABLE ");
				res.addTable(query, true);
				query.append(" DROP ");
				input.getItem().addName(query, false, true);
				query.append(" ");
				
				try(java.sql.PreparedStatement stmt = sql.getConnection().prepareStatement(query.toString())){

					stmt.execute();
				}
				resetStructure(target);
			} catch (Exception e) {
				throw new ActionException("Update failed",e);
			}
			return new ViewTableResult(target);
		}
		
	}
}