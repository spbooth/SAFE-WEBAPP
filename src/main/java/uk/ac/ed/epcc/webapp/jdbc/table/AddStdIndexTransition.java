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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.action.FormAction;
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.jdbc.SQLContext;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification.IndexType;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.Repository;

public class AddStdIndexTransition<T extends DataObjectFactory> extends EditTableFormTransition<T>{
	static final String INDEX_FORMFIELD = "Index";
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.BaseFormTransition#buildForm(uk.ac.ed.epcc.webapp.forms.Form, java.lang.Object, uk.ac.ed.epcc.webapp.AppContext)
	 */
	public void buildForm(Form f, T target, AppContext conn)
			throws TransitionException {
		TableSpecification spec = target.getTableSpecification();
		if( spec == null) {
			return;
		}
		Repository res = getRepository(target);
		Map<String,IndexType> indexes = new LinkedHashMap<>();
		for(Iterator<IndexType> it = spec.getIndexes(); it.hasNext();){
			IndexType type = it.next();
			indexes.put(type.getName(), type);
		}
		f.addInput(INDEX_FORMFIELD, "Index to add", new OptionalIndexInput(res, indexes));
		f.addAction("Add", new AddIndexAction(target));
	}
	public class AddIndexAction extends FormAction {

		private final T target;
		public AddIndexAction(T target) {
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
				String name = (String) f.get(INDEX_FORMFIELD);
				IndexType type = (IndexType) f.getItem(INDEX_FORMFIELD);
				List<Object> args = new LinkedList<>();
				
				type.accept(sql.getCreateVisitor(query, args));
				Logger log = res.getContext().getService(LoggerService.class).getLogger(getClass());
				log.debug("Query is "+query.toString());
				try(java.sql.PreparedStatement stmt = sql.getConnection().prepareStatement(query.toString())){
					int pos=1;
					for(Object o: args){
						stmt.setObject(pos++, o);
					}
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