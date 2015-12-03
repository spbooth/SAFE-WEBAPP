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
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractFormTransition;
import uk.ac.ed.epcc.webapp.jdbc.SQLContext;
import uk.ac.ed.epcc.webapp.model.data.Repository;
import uk.ac.ed.epcc.webapp.model.data.forms.inputs.RepositoryIndexInput;



public class DropIndexTransition<T extends TableStructureTransitionTarget> extends AbstractFormTransition<T>{
    /**
	 * 
	 */
	private static final String INDEX_FIELD = "Index";
	private final Repository res;
    public DropIndexTransition(Repository res){
    	this.res=res;
    }
	public void buildForm(Form f, T target, AppContext c)
			throws TransitionException {
		f.addInput(INDEX_FIELD, "Index to drop", new RepositoryIndexInput(res));
		f.addAction("Drop", new DropAction(target));
	}
	public class DropAction extends FormAction{
		private T target;
		public DropAction(T target){
			super();
			setConfirm("drop_index");
			this.target=target;
		}
		@Override
		public FormResult action(Form f) throws ActionException {
			RepositoryIndexInput input = (RepositoryIndexInput) f.getInput(INDEX_FIELD);
			try {
				SQLContext sql = res.getSQLContext();
				StringBuilder query = new StringBuilder();
				query.append("ALTER TABLE ");
				res.addTable(query, true);
				query.append(" DROP INDEX ");
				sql.quote(query, input.getValue());
				java.sql.PreparedStatement stmt = sql.getConnection().prepareStatement(query.toString());
				
				stmt.execute();
				stmt.close();
				target.resetStructure();
				Repository.reset(res.getContext(), res.getTag());
			} catch (Exception e) {
				throw new ActionException("Update failed",e);
			}
			return new ViewTableResult(target);
		}
		
	}
}