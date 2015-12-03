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
package uk.ac.ed.epcc.webapp.model.far;

import java.util.Set;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.exceptions.InvalidArgument;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactoryCreator;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.NameFinder;
import uk.ac.ed.epcc.webapp.model.ParseFactory;
import uk.ac.ed.epcc.webapp.model.data.BasicType;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.FilterResult;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.filter.SQLValueFilter;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** Each {@link DynamicForm} instance represents a single version of
 * a muti-section questionnaire that will be presented to a user.
 * <p>
 * {@link DynamicForm}s are edited and dynamically but need to be <b>frozen</b> to generate a fixed
 * structure before being presented to users. New versions can still be constructed by copying 
 * a frozen form.
 * <p>
 * All support classes and {@link TransitionFactory}s used to handle the forms produced by this factory
 * are generated from here wither via {@link #getChildManager()} or {@link #getTransitionProvider(String)}
 * table names are derived from the table used by this class. Therefore by sub-classing with a new table-name
 * an entirely distinct set of support tables will be generated.  
 * @author spb
 * @param <F> type of {@link DynamicForm}
 *
 */

public class DynamicFormManager<F extends DynamicFormManager.DynamicForm> extends PartOwnerFactory<F> implements ParseFactory<F>, TransitionFactoryCreator<TransitionFactory>{
	public static class Status extends BasicType<Status.Value>{
		/**
		 * @param field
		 */
		protected Status() {
			super("Status");
		}

		public class Value extends BasicType.Value{

			/**
			 * @param parent
			 * @param tag
			 * @param name
			 */
			protected Value(String tag, String name) {
				super(Status.this, tag, name);
			}
			
		}
	}
	private static final Status status=new Status();
	private static final Status.Value COMPOSE = status.new Value("C","Compose");
	private static final Status.Value FROZEN = status.new Value("F","Frozen");
	private static final Status.Value RETIRED = status.new Value("R","Retired");
	public static final String NAME_FIELD = "Name";
	
	public static final String PART_TAG = "Part";
	public static final String  FORM_TAG = "Form";
	
	public class DynamicForm extends PartOwner{
		private final DynamicFormManager manager;
		/**
		 * @param r
		 */
		protected DynamicForm(DynamicFormManager manager, Record r) {
			super(r);
			this.manager=manager;
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.far.PartOwner#getForm()
		 */
		@Override
		public DynamicForm getForm() {
			return this;
		}
		public DynamicFormManager getManager(){
			return manager;
		}
		
		public String getName(){
			return record.getStringProperty(NAME_FIELD);
		}
		public boolean canEdit(SessionService<?> sess){
			return DynamicFormManager.this.canEdit(sess);
		}
		public boolean canView(SessionService<?> sess){
			return DynamicFormManager.this.canEdit(sess);
		}
		public boolean isFrozen(){
			return record.getProperty(status) != COMPOSE;
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.far.PartOwner#getViewResult()
		 */
		@Override
		public FormResult getViewResult() {
			return manager.getDynamicFormProvider().new ViewResult(this);
		}
		
	}
	/**
	 * 
	 */
	public DynamicFormManager(AppContext conn,String homeTable) {
		setContext(conn, homeTable);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.DataObjectFactory#makeBDO(uk.ac.ed.epcc.webapp.model.data.Repository.Record)
	 */
	@Override
	protected DataObject makeBDO(Record res) throws DataFault {
		return new DynamicForm(this,res);
	}
	
	
	public Class getTarget(){
		return DynamicForm.class;
	}


	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.far.PartOwnerFactory#getChildManager()
	 */
	@Override
	public PageManager getChildManager() {
		return new PageManager(this);
	}


	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.transition.TransitionFactoryCreator#getTransitionProvider(java.lang.String)
	 */
	@Override
	public TransitionFactory getTransitionProvider(String tag) {
		if( tag.equals(PART_TAG)){
			return new PartPathTransitionProvider(getTag()+TransitionFactoryCreator.TYPE_SEPERATOR+tag, this);
		}else if( tag .equals(FORM_TAG)){
			return new DynamicFormTransitionProvider(getTag()+TransitionFactoryCreator.TYPE_SEPERATOR+tag, this);
		}
		return null;
	}


	@Override
	protected TableSpecification getDefaultTableSpecification(AppContext c,
			String table) {
		TableSpecification spec = new TableSpecification("FormID");
		spec.setField(NAME_FIELD, new StringFieldType(false, null, 64));
		spec.setField(status.getField(), status.getFieldType(COMPOSE));
		try {
			spec.new Index("NameIndex", true, NAME_FIELD);
		} catch (InvalidArgument e) {
			c.getService(LoggerService.class).getLogger(getClass()).error("Error making index",e);
		}
		return spec;
	}
	public F findFromString(String name) {
		try {
			return find(getNameFilter(name),true);
		} catch (DataException e) {
			getLogger().error("Error in bindByName",e);
			return null;
		}
	}

	/**
	 * @param name
	 * @return
	 */
	public SQLValueFilter<F> getNameFilter(String name) {
		return new SQLValueFilter<F>(getTarget(), res, NAME_FIELD, name);
	}
	public FilterResult<F> getActive() throws DataFault{
		return new FilterSet(status.getExcludeFilter(this, RETIRED));
	}
	/** Is the specified person allowed to perform targetless operations.
	 * 
	 * @param sess
	 * @return
	 */
	public boolean canEdit(SessionService<?> sess){
		return sess.hasRole(SessionService.ADMIN_ROLE);
	}

	@Override
	protected Set<String> getSupress() {
		Set<String> supress = super.getSupress();
		supress.add(status.getField());
		return supress;
	}
	/**
	 * @return
	 */
	public PartPathTransitionProvider getPartPathProvider() {
		return (PartPathTransitionProvider) getTransitionProvider(DynamicFormManager.PART_TAG);
	}
	public DynamicFormTransitionProvider<F> getDynamicFormProvider(){
		return (DynamicFormTransitionProvider<F>) getTransitionProvider(FORM_TAG);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.ParseFactory#getName(java.lang.Object)
	 */
	@Override
	public String getCanonicalName(F object) {
		return object.getName();
	}
}