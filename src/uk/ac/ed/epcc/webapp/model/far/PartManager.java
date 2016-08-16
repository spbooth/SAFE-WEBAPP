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

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.content.Table;
import uk.ac.ed.epcc.webapp.content.UIGenerator;
import uk.ac.ed.epcc.webapp.exceptions.InvalidArgument;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.action.FormAction;
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.forms.factory.FormUpdate;
import uk.ac.ed.epcc.webapp.forms.inputs.TextInput;
import uk.ac.ed.epcc.webapp.forms.result.ChainedTransitionResult;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.expr.FuncExpression;
import uk.ac.ed.epcc.webapp.jdbc.expr.SQLFunc;
import uk.ac.ed.epcc.webapp.jdbc.expr.ValueResultMapper;
import uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor;
import uk.ac.ed.epcc.webapp.jdbc.filter.MatchCondition;
import uk.ac.ed.epcc.webapp.jdbc.filter.OrderClause;
import uk.ac.ed.epcc.webapp.jdbc.filter.OrderFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLAndFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.jdbc.table.IntegerFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.ReferenceFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.model.data.FilterResult;
import uk.ac.ed.epcc.webapp.model.data.ReferenceFilter;
import uk.ac.ed.epcc.webapp.model.data.Repository;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.filter.FilterDelete;
import uk.ac.ed.epcc.webapp.model.data.filter.SQLValueFilter;
import uk.ac.ed.epcc.webapp.model.data.filter.SelfReferenceFilter;
import uk.ac.ed.epcc.webapp.model.data.forms.Creator;
import uk.ac.ed.epcc.webapp.model.data.forms.Updater;
import uk.ac.ed.epcc.webapp.model.data.transition.AbstractViewTransitionFactory.ViewResult;
import uk.ac.ed.epcc.webapp.model.far.DynamicFormManager.DynamicForm;
import uk.ac.ed.epcc.webapp.model.far.handler.PartConfigFactory;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** abstract super-class for a part or sub-part of a Form.
 * There may be several levels in the hierarchy so parts also implement {@link PartOwner}
 * <p>
 * Each part references its parent, has an order field that
 * determines the order of parts within the parent and has a name field that is unique
 * within the parent.
 * <p>
 * The factory classes are expected to always be referenced via their parent
 * and their identifying tags are derived from the parent tags so that if multiple
 * tables of forms are created they automatically generate independent tables of parts
 * this makes it easier to remove entire classes of form.
 *  
 * @author spb
 * @param <O> type of owner
 * @param <P> type of part.
 *
 */

public abstract class PartManager<O extends PartOwner,P extends PartManager.Part<O>> extends PartOwnerFactory<P> {
	
	@Override
	protected Set<String> getSupress() {
		Set<String> supress = super.getSupress();
		supress.add(OWNER_FIELD);
		supress.add(ORDER_FIELD);
		return supress;
	}
	
	/**
	 * 
	 */
	private static final int MAX_NAME_LENGTH = 32;
	private static final int MIN_NAME_LENGTH = 4;
	public static final String OWNER_FIELD="Owner";
	public static final String ORDER_FIELD="SortOrder";
	public static final String NAME_FIELD="Name";
	Pattern name_pattern = Pattern.compile("[A-Za-z][A-Za-z0-9]*");
	private final PartOwnerFactory<O> owner_fac;
	protected final DynamicFormManager<?> form_manager;
	private final String part_tag;
	
	public PartManager(DynamicFormManager<?> form_manager,PartOwnerFactory<O> owner_fac, String part_tag){
		this.form_manager=form_manager;
		this.owner_fac=owner_fac;
		this.part_tag=part_tag;
		setContext(owner_fac.getContext(), owner_fac.getTag()+part_tag);
	}
	
	public abstract static class Part<O extends PartOwner> extends PartOwner implements UIGenerator{
		private final PartManager<O,?> manager;
		protected Part(PartManager<O, ?> manager,Record r) {
			super(r);
			this.manager=manager;
		}
		/** get the {@link PartManager} for this part.
		 * 
		 * @return
		 */
		@Override
		public PartManager<O,? extends Part<O>> getFactory(){
			return manager;
		}
		public O getOwner(){
			return manager.owner_fac.find(getOwnerID());
		}
		
		

		/**
		 * @return
		 */
	    Number getOwnerID() {
			return record.getNumberProperty(OWNER_FIELD);
		}
		public Number getSortOrder(){
			return record.getNumberProperty(ORDER_FIELD);
		}
		public void setSortOrder(int order){
			record.setProperty(ORDER_FIELD, order);
		}
		public final DynamicForm getForm(){
			return getOwner().getForm();
		}
		public String getName(){
			return getRawName();
		}
		public String getRawName(){
			return record.getStringProperty(NAME_FIELD,manager.getPartTag()+Integer.toString(getID()));
		}
		void setName(String name){
			record.setProperty(NAME_FIELD, name);
		}
		void setOwner(O owner){
			record.setProperty(OWNER_FIELD, owner.getID());
		}
		public String getSpacedName() {
			String name = "";
			String name_parts[] = getRawName().split("(?=[A-Z])");
			boolean single_capitals = false;
			for (int i = 0; i < name_parts.length; i++) {
				if (name_parts[i].length() == 1) {
					if (single_capitals) {
						name += name_parts[i];
					}
					else {
						single_capitals = true;
						name += " " + name_parts[i];
					}
				}
				else {
					single_capitals = false;
					name += " " + name_parts[i];
				}
		    }
			return name;
		}
		
		public abstract String getTypeName();
		@Override
		public ContentBuilder addContent(ContentBuilder builder) {
			builder.addLink(getContext(), getSpacedName(), getViewResult());
			return builder;
		}
		
		public ViewResult getViewResult() {
			return manager.form_manager.getPartPathProvider().new ViewResult(this);
		}
		/** build a form that edits and validates legal configuration values.
		 * 
		 * @param f
		 */
		public void makeConfigForm(Form f){
			return;
		}
		/** Does this object support a config operation.
		 * 
		 * @return
		 */
		public boolean  hasConfig(){
			return getFactory().getConfigFactory() != null;
		}
		/** get a map of additional information to be displayed
		 * 
		 * @return
		 */
		public Map<String,Object> getInfo(){
			return new LinkedHashMap<String, Object>();
		}
		
		/** visit a {@link PartVisitor}
		 * 
		 * @param vis
		 * @return
		 */
		public abstract <X> X visit(PartVisitor<X> vis);
		
		
		
	}
	public class PartUpdater extends Updater<P>{

		@Override
		public void customiseUpdateForm(Form f, P o) {
			super.customiseUpdateForm(f, o);
			f.addInput(NAME_FIELD, "Name", new UnusedNameInput(o.getOwner(), o.getName()));
		}

		@Override
		public FormResult getResult(String typeName, P dat, Form f) {
			if( dat.hasConfig()){
				return new ChainedTransitionResult<P, PartTransitionKey<P>>(form_manager.getPartPathProvider(), dat, PartPathTransitionProvider.CONFIG);
			}else{
				// we may have edited from a state that had config to one that did not
				PartConfigFactory<O, P> config = (PartConfigFactory<O, P>) dat.getFactory().getConfigFactory();
				if( config != null ){
					try {
						config.clearAll(dat);
					} catch (DataFault e) {
						getLogger().error("Error clearing config", e);
					}
				}
			}
			return dat.getViewResult();
		}

		/**
		 * @param dataObjectFactory
		 */
		public PartUpdater() {
			super(PartManager.this);
		}
		
	}

	@Override
	public FormUpdate<P> getFormUpdate(AppContext c) {
		return new PartUpdater();
	}

	public PartCreator getChildCreator( O owner){
		return new PartCreator(owner);
	}
	public class PartCreator extends Creator<P>{
		@Override
		protected Map<String, Object> getSelectors() {
			Map<String, Object> selectors = super.getSelectors();
			selectors.put(NAME_FIELD,new UnusedNameInput(owner,null));
			return selectors;
		}

		@Override
		public void setAction(String type_name, Form f) {
			f.addAction("Create", new ChildCreateAction(owner));
		}

		/**
		 * @param dataObjectFactory
		 * @param owner
		 */
		public PartCreator(O owner) {
			super(PartManager.this);
			this.owner = owner;
		}

		private final O owner;
		
	}
	public final String getPartTag(){
		return part_tag;
	}
	public Class<? super P> getTarget(){
		return Part.class;
	}

	
	@Override
	protected TableSpecification getDefaultTableSpecification(AppContext c,
			String table) {
		TableSpecification spec = new TableSpecification("PartID");
		spec.setField(OWNER_FIELD, new ReferenceFieldType(owner_fac.getTag()));
		spec.setField(ORDER_FIELD, new IntegerFieldType(false, 0));
		spec.setField(NAME_FIELD, new StringFieldType(false, null, MAX_NAME_LENGTH));
		
		try {
			// Names should be unique within a parent
			// also gives a search index on owner.
			spec.new Index("NameIndex", true, OWNER_FIELD,NAME_FIELD);
		} catch (InvalidArgument e) {
			getLogger().error("Problem making index", e);
		}
		return spec;
	}
	/** 
	 * 
	 * @return
	 */
	public PartOwnerFactory<O> getOwnerFactory(){
		return owner_fac;
	}
	
	
	/** Get all the parts belonging to an owner.
	 * 
	 * @param owner
	 * @return
	 * @throws DataFault
	 */
	public FilterResult<P> getParts(O owner) throws DataFault{
		return new FilterSet(getOwnerFilter(owner));
	}


	/**
	 * @param owner
	 * @return
	 */
	private ReferenceFilter<P, O> getOwnerFilter(O owner) {
		return new ReferenceFilter<P, O>(this, OWNER_FIELD, owner);
	}
	
	public P findByParentAndName(O owner,String name) throws DataException{
		SQLAndFilter<P> fil = new SQLAndFilter<P>(getTarget());
		fil.addFilter(getOwnerFilter(owner));
		fil.addFilter(new SQLValueFilter<P>(getTarget(), res, NAME_FIELD, name));
		return find(fil,true);
	}
	private class MaxFinder extends AbstractFinder<Number>{
		private MaxFinder(){
			setMapper(new ValueResultMapper<Number>(FuncExpression.apply(getContext(),SQLFunc.MAX,Number.class,res.getNumberExpression(getTarget(), Number.class, ORDER_FIELD))));
		}
	}
	private int getNextPosition(O owner) throws DataException{
		MaxFinder finder = new MaxFinder();
		Number n = finder.find(getOwnerFilter(owner), true);
		if( n == null ){
			return 1;
		}
		return n.intValue()+1;
	}
	public class UnusedNameInput extends TextInput{
		private final O parent;
		private final String existing;
		public UnusedNameInput(O parent,String existing){
			this.parent=parent;
			this.existing=existing;
			setOptional(false);
			setMaxResultLength(MAX_NAME_LENGTH);
		}
		@Override
		public void validate() throws FieldException {
			super.validate();
			if( existing != null && existing.equals(getValue())){
				return;
			}
			if( ! name_pattern.matcher(getValue()).matches()){
				throw new ValidateException("Invalid characters");
			}
			if( getValue().trim().length() < MIN_NAME_LENGTH){
				throw new ValidateException("Name too short must be at least "+MIN_NAME_LENGTH);
			}
			try {
				P match = findByParentAndName(parent, getValue());
				if( match != null  ){
					throw new ValidateException("Name already in use");
				}
			} catch (DataException e) {
				throw new ValidateException("Internal error", e);
			}
		}
		
	}
	private class PartOrderFilter implements OrderFilter<P>, SQLFilter<P>{
		private LinkedList<OrderClause> order;
		/** create filter that generates canonical sort order.
		 * @param down should order be reversed
		 * 
		 */
		public PartOrderFilter(boolean down) {
			order = new LinkedList<OrderClause>();
			order.add(res.getOrder(ORDER_FIELD, down));
			order.add(res.getOrder(null, down));
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter#acceptVisitor(uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor)
		 */
		@Override
		public <X> X acceptVisitor(FilterVisitor<X, ? extends P> vis)
				throws Exception {
			return vis.visitOrderFilter(this);
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.Targetted#getTarget()
		 */
		@Override
		public Class<? super P> getTarget() {
			return PartManager.this.getTarget();
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.OrderFilter#OrderBy()
		 */
		@Override
		public List<OrderClause> OrderBy() {
			return order;
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter#accept(java.lang.Object)
		 */
		@Override
		public void accept(P o) {
			
		}
		
	}
	/** get the next sibling (offspring of same parent)
	 * 
	 * @param current  start position.
	 * @param up direction to move
	 * @return sibling or null
	 * @throws DataFault
	 */
	public P getSibling(P current, boolean up) throws DataFault{
		SQLAndFilter<P> fil = new SQLAndFilter<P>(getTarget());
		fil.addFilter(new SQLValueFilter<P>(getTarget(), res, OWNER_FIELD, current.getOwnerID()));
		fil.addFilter(new SQLValueFilter<P>(getTarget(), res, ORDER_FIELD, up ? MatchCondition.GE : MatchCondition.LE, current.getSortOrder()));
		fil.addFilter(new SelfReferenceFilter<P>(getTarget(), res, true, makeReference(current)));
		fil.addFilter(new PartOrderFilter(! up));
		FilterIterator it = new FilterIterator(fil, 0, 1);
		if( it.hasNext()){
			return it.next();
		}
		return null;
	}
	public P getFirst(O owner) throws DataFault{
		FilterIterator it = new FilterIterator(getOwnerFilter(owner), 0, 1);
		if( it.hasNext()){
			return it.next();
		}
		return null;
	}

	@Override
	protected List<OrderClause> getOrder() {
		List<OrderClause> order = super.getOrder();
		order.add(res.getOrder(ORDER_FIELD, false));
		order.add(res.getOrder(null, false));
		return order;
	}
	public class ChildCreateAction extends FormAction{
		/**
		 * @param parent
		 */
		public ChildCreateAction(O parent) {
			super();
			this.parent = parent;
		}
		
		private final O parent;
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.action.FormAction#action(uk.ac.ed.epcc.webapp.forms.Form)
		 */
		@Override
		public FormResult action(Form f) throws ActionException {
			Logger log = getLogger();
	        
			P o = null;
			log.debug("In create action");
			try {
				// populate as a record in case factory is polymorphic and needs form parameters to create object.
				Repository.Record rec = makeRecord();
				// we may have default values supressed in the form
				Map<String,Object> defs = getDefaults();
				if( defs != null){
					log.debug("set default contents");
					rec.putAll(defs);
				}
				rec.putAll(f.getContents());
				rec.put(OWNER_FIELD, parent.getID());
				rec.put(ORDER_FIELD,getNextPosition(parent));
				o = (P) makeBDO(rec);
				log.debug("set form contents");
				
				o.commit();
				if( o.hasConfig()){
					return new ChainedTransitionResult<P, PartTransitionKey>(form_manager.getPartPathProvider(), o, PartPathTransitionProvider.CONFIG);
				}
				return o.getViewResult();
				
			} catch (Exception e) {
				log.error("exception in CreatChildAction",e);
				throw new ActionException("Create failed", e);
			}
			
		}
		
	}
	
	public Table getChildTable(P target) throws DataFault{
		PartManager<P, Part<P>> child = getChildManager();
		if( child == null ){
			return null;
		}
		return child.getPartTable(target);
	}
	
	public Table getPartTable( O owner) throws DataFault{
		Table t = new Table();
		for(P part : getParts(owner)){
			t.put(getPartTag(), part, part);
			Map<String,Object> info = part.getInfo();
			for(String key : info.keySet()){
				Object value = info.get(key);
				if( value instanceof String && ((String)value).length() > 35){
					value = ((String)value).substring(0, 32)+" ...";
				}
				t.put(key, part, value);
			}
			// re-order buttons
			PartPathTransitionProvider<O,P> provider = form_manager.getPartPathProvider();
			provider.addMoveButtons(t,part,getContext().getService(SessionService.class));
		}
		return t;
	}
	/** recursively delete any parts with a specified owner
	 * 
	 * @param owner
	 * @throws DataFault 
	 */
	public final void deleteAll(O owner) throws DataFault{
		
		for(P part : getParts(owner)){
			deleteContent(part);
		}
		// Now delete the parts themselves.
		FilterDelete<P> del = new FilterDelete<P>(res);
		del.delete(getOwnerFilter(owner));
	}
	/** delete a {@link Part}
	 * 
	 * @param part
	 * @throws DataFault
	 */
	public final void deletePart(P part) throws DataFault{
		deleteContent(part);
		part.delete();
	}
	/** prepare a {@link Part} for deletion by removing any content.
	 * This defaults to recursing to child elements but may need to be extended
	 * for levels that store additional data in other tables.
	 * @param part
	 * @throws DataFault 
	 */
	public void deleteContent(P part) throws DataFault{
		PartManager child = getChildManager();
		if( child != null ){
			child.deleteAll(part);
		}
	}
	private PartManager child_manager=null;
	public abstract String getChildTypeName();
	protected abstract PartManager makeChildManager();
	@Override
	public final PartManager getChildManager() {
		if( child_manager == null ){
			child_manager=makeChildManager();
		}
		return child_manager;
	}
	
	private PartConfigFactory<O, P> config_factory=null;
	protected PartConfigFactory<O,P> makeConfigFactory(){
		return null;
	}
	public PartConfigFactory<O, P> getConfigFactory(){
		if( config_factory == null ){
			config_factory = makeConfigFactory();
		}
		return config_factory;
	}
	/** create a (uncommitted) duplicate copy of a part with a new owner
	 * 
	 * @param new_owner
	 * @param original
	 * @return
	 * @throws DataFault 
	 */
	public P duplicate(O new_owner, P original) throws DataFault{
		P result = makeBDO();
		result.setOwner(new_owner);
		result.setName(original.getName());
		result.setSortOrder(original.getSortOrder().intValue());
		return result;
	}
	
}