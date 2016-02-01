//| Copyright - The University of Edinburgh 2015                            |
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.config.ConfigService;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.action.FormAction;
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.inputs.ItemInput;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractFormTransition;
import uk.ac.ed.epcc.webapp.forms.transition.FormTransition;
import uk.ac.ed.epcc.webapp.forms.transition.Transition;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionVisitor;
import uk.ac.ed.epcc.webapp.jdbc.SQLContext;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification.IndexType;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.data.Repository;
import uk.ac.ed.epcc.webapp.model.data.Repository.FieldInfo;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.transition.TransitionKey;
/** A {@link TransitionSource} that depends on the default {@link TableSpecification}
 * for the target table. For example to add missing fields from the specification.
 * @author spb
 *
 * @param <T>
 */
public class TableSpecificationTransitionSource<T extends TableStructureTransitionTarget> implements
		TransitionSource<T> {

	/**
	 * 
	 */
	static final String ADD_STD_INDEX = "Add Std index";
	/**
	 * 
	 */
	static final String ADD_STD_FIELD = "Add Std field";
	static final String DROP_OPTIONAL_FIELD ="Drop optional field";
	private Repository res;
	private TableSpecification spec;
	static final String FIELD_FORMFIELD = "Field";
	static final String INDEX_FORMFIELD = "Index";
	public class AddStdFieldTransition extends AddFieldTransition<T>{

		public AddStdFieldTransition(Repository res) {
			super(res);
		}

		@Override
		protected void addFormParams(Form f, AppContext c) {
			
			f.addInput(FIELD_FORMFIELD, "Field to add", new OptionalFieldInput<FieldType>(res,true,  spec.getStdFields()));
			
		}

		@Override
		protected FieldType getFieldType(Form f) {
			return (FieldType) f.getItem(FIELD_FORMFIELD);
		}
		
	}
	public class DropOptionalFieldTransition extends DropFieldTransition<T>{

		public DropOptionalFieldTransition(Repository res) {
			super(res);
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.table.DropFieldTransition#getFieldInput()
		 */
		@Override
		public <I extends Input & ItemInput<FieldInfo>> I getFieldInput() {
			Map<String,FieldInfo> map = new LinkedHashMap<String, Repository.FieldInfo>();
			for(String name : spec.getOptionalFieldNames()){
				FieldInfo info = res.getInfo(name);
				if( info != null){
					map.put(name, info);
				}
			}
			return (I) new OptionalFieldInput<FieldInfo>(res, false, map);
		}

		
		
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
				SQLContext sql = res.getSQLContext();
				StringBuilder query = new StringBuilder();
				query.append("ALTER TABLE ");
				res.addTable(query, true);
				query.append(" ADD ");
				String name = (String) f.get(INDEX_FORMFIELD);
				IndexType type = (IndexType) f.getItem(INDEX_FORMFIELD);
				List<Object> args = new LinkedList<Object>();
				
				type.accept(sql.getCreateVisitor(query, args));
				Logger log = res.getContext().getService(LoggerService.class).getLogger(getClass());
				log.debug("Query is "+query.toString());
				java.sql.PreparedStatement stmt = sql.getConnection().prepareStatement(query.toString());
				int pos=1;
				for(Object o: args){
					stmt.setObject(pos++, o);
				}
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
	public class AddStdIndexTransition extends AbstractFormTransition<T>{
		
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.transition.BaseFormTransition#buildForm(uk.ac.ed.epcc.webapp.forms.Form, java.lang.Object, uk.ac.ed.epcc.webapp.AppContext)
		 */
		public void buildForm(Form f, T target, AppContext conn)
				throws TransitionException {
			Map<String,IndexType> indexes = new LinkedHashMap<String, TableSpecification.IndexType>();
			for(Iterator<IndexType> it = spec.getIndexes(); it.hasNext();){
				IndexType type = it.next();
				indexes.put(type.getName(), type);
			}
			f.addInput(INDEX_FORMFIELD, "Index to add", new OptionalIndexInput(res, indexes));
			f.addAction("Add", new AddIndexAction(target));
		}
	}
	public TableSpecificationTransitionSource(Repository res,TableSpecification spec){
		this.res=res;
		this.spec=spec;
	}
	
	
	public Map<TransitionKey<T>, Transition<T>> getTransitions() {
		Map<TransitionKey<T>,Transition<T>> result = new HashMap<TransitionKey<T>, Transition<T>>();
		result.put(new AdminOperationKey<T>(ADD_STD_FIELD,"Add missing fields from the default table specification for this class"),
				new AddStdFieldTransition(res));
		result.put(new AdminOperationKey<T>(ADD_STD_INDEX, "Add missing index from the default table specification for this class"), 
				new AddStdIndexTransition());
		result.put(new AdminOperationKey<T>(DROP_OPTIONAL_FIELD,"Drop optional existing field"),new DropOptionalFieldTransition(res));
		return result;
	}

}