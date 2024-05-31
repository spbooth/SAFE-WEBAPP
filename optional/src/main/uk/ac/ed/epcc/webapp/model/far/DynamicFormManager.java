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

import java.util.*;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.XMLPrinter;
import uk.ac.ed.epcc.webapp.exceptions.InvalidArgument;
import uk.ac.ed.epcc.webapp.forms.inputs.ClassInput;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.inputs.OptionalListInputWrapper;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.MessageResult;
import uk.ac.ed.epcc.webapp.forms.result.WarningMessageResult;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactoryCreator;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.model.ParseFactory;
import uk.ac.ed.epcc.webapp.model.data.BasicType;
import uk.ac.ed.epcc.webapp.model.data.FilterResult;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.filter.SQLValueFilter;
import uk.ac.ed.epcc.webapp.model.data.forms.Selector;
import uk.ac.ed.epcc.webapp.model.data.stream.ByteArrayMimeStreamData;
import uk.ac.ed.epcc.webapp.model.data.stream.MimeStreamData;
import uk.ac.ed.epcc.webapp.model.far.DynamicFormManager.Status.Value;
import uk.ac.ed.epcc.webapp.model.far.response.CompleteVisitor;
import uk.ac.ed.epcc.webapp.model.serv.ServeDataProducer;
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

public class DynamicFormManager<F extends DynamicFormManager.DynamicForm> extends PartOwnerFactory<F> implements ParseFactory<F>, TransitionFactoryCreator<TransitionFactory>, ServeDataProducer{
	public static class Status extends BasicType<Status.Value>{
		
		public Status() {
			super("Status");
		}

		public class Value extends BasicType.Value{

			/*
			 * @param tag
			 * @param name
			 */
			public Value(String tag, String name) {
				super(Status.this, tag, name);
			}
			
		}
	}
	private static final Status status=new Status();
	private static final Status.Value NEW = status.new Value("N","New");
	private static final Status.Value ACTIVE = status.new Value("A","Active");
	private static final Status.Value RETIRED = status.new Value("R","Retired");
	
		
	public static final String NAME_FIELD = "Name";
	public static final String VALIDATING_VISITOR_FIELD = "ValidatingVisitor";
	
	public static final String PART_TAG = "Part";
	public static final String  FORM_TAG = "Form";
	
	public static class DynamicForm extends PartOwner{
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
		
		@Override
		public String getIdentifier(int maxLength) {
			return getName();
		}
		
		public String getName(){
			return record.getStringProperty(NAME_FIELD);
		}
		void setName(String name){
			record.setProperty(NAME_FIELD, name);
		}
		public boolean canEdit(SessionService<?> sess){
			return manager.canEdit(sess) && isNew();
		}
		public boolean canView(SessionService<?> sess){
			return manager.canEdit(sess);
		}
		public boolean isNew(){
			return record.getProperty(status) == NEW;
		}
		public boolean isActive(){
			return record.getProperty(status) == ACTIVE;
		}
		public boolean isRetired(){
			return record.getProperty(status) == RETIRED;
		}
		
		public String getStatusName(){
			return getStatus().getName();
		}
		public Value getStatus(){
			return record.getProperty(status);
		}
		public void setStatus(Status.Value val){
			record.setProperty(status, val);
		}
		
		
		public MessageResult renew() throws Exception {
			MessageResult msg = new MessageResult("form_renewal_failed");
			
			if (isActive()) {
				setStatus(NEW);
				commit();
				msg = new MessageResult("form_renewed");
			}
			
			return msg;
		}
		
		public MessageResult activate() throws Exception {
			MessageResult msg = new WarningMessageResult("form_activation_failed");
			
			if (isNew() || isRetired()) {
				setStatus(ACTIVE);
				commit();
				msg = new MessageResult("form_activated");
			}
			
			return msg;		
		}
		
		public MessageResult retire() throws Exception {
			MessageResult msg = new MessageResult("form_retiral_failed");
			
			if (isActive()) {
				setStatus(RETIRED);
				commit();
				msg = new MessageResult("form_retired");
			}
			
			return msg;
		}
		
		
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.far.PartOwner#getViewResult()
		 */
		@Override
		public FormResult getViewResult() {
			return manager.getDynamicFormProvider().new ViewResult(this);
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.far.PartOwner#getPartOwnerFactory()
		 */
		@Override
		public DynamicFormManager getFactory() {
			return manager;
		}
		
		/** get the visitor class to use for completion tests
		 * 
		 * @return
		 */
		public Class<? extends CompleteVisitor> getCompleteVisitor(){
			String tag = getCompleteVisitorTag();
			if( tag == null ){
				return CompleteVisitor.class;
			}
			return getContext().getPropertyClass(CompleteVisitor.class, tag);
		}

		String getCompleteVisitorTag() {
			return record.getStringProperty(VALIDATING_VISITOR_FIELD,getContext().getInitParameter("dynamic_form.default.validator"));
		}
		void setCompleteVisitorTag(String tag){
			record.setOptionalProperty(VALIDATING_VISITOR_FIELD, tag);
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
	protected F makeBDO(Record res) throws DataFault {
		return (F) new DynamicForm(this,res);
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
		
		spec.setField(status.getField(), status.getFieldType(NEW));
		spec.setOptionalField(VALIDATING_VISITOR_FIELD, new StringFieldType(true,null,128));
		try {
			spec.new Index("NameIndex", true, NAME_FIELD);
		} catch (InvalidArgument e) {
			Logger.getLogger(c,getClass()).error("Error making index",e);
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
		return new SQLValueFilter<F>(res, NAME_FIELD, name);
	}
	
	public FilterResult<F> getNew() throws DataFault{
		return getResult(getNewFilter());
	}
	public SQLFilter<F> getNewFilter() {
		return status.getFilter(this, NEW);
	}
	
	public FilterResult<F> getActive() throws DataFault{
		return getResult(getActiveFilter());
	}
	public BaseFilter<F> getActiveFilter() {
		return status.getFilter(this, ACTIVE);
	}
	
	public FilterResult<F> getRetired() throws DataFault{
		return getResult(getRetiredFilter());
	}
	public SQLFilter<F> getRetiredFilter() {
		return status.getFilter(this, RETIRED);
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
	@Override
	protected Map<String, Selector> getSelectors() {
		Map<String, Selector> selectors = super.getSelectors();
		
		selectors.put(VALIDATING_VISITOR_FIELD, new Selector() {

			@Override
			public Input getInput() {
				ClassInput<CompleteVisitor> input =  new ClassInput<>(getContext(), CompleteVisitor.class);
				OptionalListInputWrapper<String, Class<? extends CompleteVisitor>> optional_input = new OptionalListInputWrapper<>(input,"use default");
				return input;
			}
			
		});
		
		return selectors;
	}

	@Override
	protected Set<String> getOptional() {
		Set<String> optional = new HashSet<>();
		optional.add(VALIDATING_VISITOR_FIELD);
		return optional;
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

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.serv.ServeDataProducer#getData(uk.ac.ed.epcc.webapp.session.SessionService, java.util.List)
	 */
	@Override
	public MimeStreamData getData(SessionService user, List<String> path) throws Exception {
		LinkedList<String> list;
		if( path instanceof LinkedList){
			list=(LinkedList<String>) path;
		}else{
			list=new LinkedList<>(path);
		}
		
		F form = find(Integer.parseInt(list.pop()));
		if( ! form.canView(user)){
			return null;
		}
		
		if( ! list.isEmpty()  && list.peekLast().endsWith(".xml")){
			// remove last element this should be the file-name
			list.removeLast();
		}
		
		XMLPrinter printer = new XMLPrinter();
		XMLVisitor<XMLPrinter> vis = new XMLVisitor<>(getContext(), printer);
		String name;
		
		if( list.isEmpty() ){
			name = form.getName();
			printer.open("Form");
				vis.visitOwner(this, form);
			printer.close();
		}else{
			PartManager.Part p =  AbstractPartTransitionProvider.getTarget(form, getChildManager(), list);
			if( p == null ){
				return null;
			}
			p.visit(vis);
			name=p.getName();
		}
		ByteArrayMimeStreamData data = new ByteArrayMimeStreamData(printer.toString().getBytes("UTF8"));
		data.setMimeType("text/xml");
		data.setName(name+".xml");
		return data;
	}

	public String getDownloadName(SessionService user, List<String> path) throws Exception{
		MimeStreamData data = getData(user, path);
		if( data == null){
			return null;
		}
		return data.getName();
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.far.PartOwnerFactory#getChildTypeName()
	 */
	@Override
	public String getChildTypeName() {
		return PageManager.PAGE_TYPE_NAME;
	}
	
	public F duplicate(F original, String name) throws DataException{
		F duplicate = makeBDO();
		duplicate.setName(name);
		duplicate.setCompleteVisitorTag(original.getCompleteVisitorTag());
		duplicate.commit();
		
		DuplicateVisitor vis = new DuplicateVisitor(duplicate);
		vis.visitOwner(this, original);
		return duplicate;
	}

	public boolean hasActive() {
		try {
			return exists(getActiveFilter());
		} catch (DataException e) {
			getLogger().error("Error checking for active forms", e);
		}
		return false;
	}
}