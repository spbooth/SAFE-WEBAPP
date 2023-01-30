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
package uk.ac.ed.epcc.webapp.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
import uk.ac.ed.epcc.webapp.forms.factory.FormCreator;
import uk.ac.ed.epcc.webapp.forms.factory.FormUpdate;
import uk.ac.ed.epcc.webapp.forms.inputs.CodeListInput;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.inputs.NameInputProvider;
import uk.ac.ed.epcc.webapp.forms.inputs.NoHtmlInput;
import uk.ac.ed.epcc.webapp.forms.inputs.NoSpaceFieldValidator;
import uk.ac.ed.epcc.webapp.forms.inputs.ParseAbstractInput;
import uk.ac.ed.epcc.webapp.forms.inputs.TypeError;
import uk.ac.ed.epcc.webapp.forms.inputs.TypeException;
import uk.ac.ed.epcc.webapp.forms.inputs.UnusedNameInput;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.*;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.HistoryFactory;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.filter.SQLValueFilter;
import uk.ac.ed.epcc.webapp.model.data.forms.Creator;
import uk.ac.ed.epcc.webapp.model.data.forms.Selector;
import uk.ac.ed.epcc.webapp.model.data.forms.Updater;
import uk.ac.ed.epcc.webapp.model.data.forms.inputs.DataObjectAlternateInput;
import uk.ac.ed.epcc.webapp.model.data.forms.inputs.DataObjectItemInput;
import uk.ac.ed.epcc.webapp.model.data.forms.inputs.DataObjectItemParseInput;
import uk.ac.ed.epcc.webapp.model.data.forms.inputs.NameFinderInput;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedDataCache;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedProducer;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedReference;
import uk.ac.ed.epcc.webapp.model.history.HistoryFieldContributor;

/** Factory class for Classification objects.
 * 
 * Table will auto create if it does not exist.
 * @author spb
 * @param <T> 
 *
 */


public class ClassificationFactory<T extends Classification> extends DataObjectFactory<T> implements Comparable<ClassificationFactory>, HistoryFieldContributor, NameFinder<T>,NameInputProvider<T>{
	
	/** Maximum size of pull-down menu in update form.
	 * 
	 */
	private static final int CLASSIFICATION_MAX_MENU = 200;
	private static final Pattern WHITESPACE = Pattern.compile("\\s");
	
	private HistoryFactory<T,HistoryFactory.HistoryRecord<T>> hist_fac=null;
	
	
	protected ClassificationFactory() {
		super();
	}
    public ClassificationFactory(AppContext ctx, String homeTable){
    	setContext(ctx, homeTable);
    }
    
    public HistoryFactory<T,HistoryFactory.HistoryRecord<T>> getHistoryFactory() {
    	if( hist_fac == null) {
    		String homeTable = getTag();
    		// create history factory if history table configured
        	String histTable = getContext().getInitParameter(homeTable + ".history_table");
        	if (histTable != null) {
        		// Allow sub-classing provided consrucor interface matches that of HistoryFactory
        		try {
    				hist_fac = getContext().makeParamObject(getContext().getPropertyClass(HistoryFactory.class, histTable), this,histTable);
    			} catch (Exception e) {
    				getLogger().error("Error making history factory "+histTable+" for "+homeTable, e);
    			}
        	}
    	}
    	return hist_fac;
    }
    
    @Override
    public TableSpecification getDefaultTableSpecification(AppContext c,String homeTable){
      TableSpecification spec = Classification.getTableSpecification(c,homeTable);
	return spec;	
    }
    
	
	
    @Override
    protected void postCreateTableSetup(AppContext c, String table){
    	String bootstrap = c.getInitParameter("classification.bootstrap."+table);
    	if( bootstrap != null){
    		for(String name : bootstrap.split(",")){
    			try{
    				T obj = makeFromString(name);
    				obj.commit();
    			}catch(Exception e){
    				c.error(e,"Error in classification bootstrap "+table);
    			}
    		}
    	}
    }
	@Override
	protected T makeBDO(Record res) throws DataFault {
		return (T) new Classification(res, this);
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.NameFinder#findByName(java.lang.String)
	 */
	@Override
	public T findFromString(String name){
		if( name == null ){
			return null;
		}
		try {
			return find(getStringFinderFilter(name),true);
		} catch (DataException e) {
			getContext().error(e,"Error in findByName");
			return null;
		}
	}
	/**
	 * @param name
	 * @return
	 */
	@Override
	public SQLValueFilter<T> getStringFinderFilter(String name) {
		return new SQLValueFilter<>(res,Classification.NAME,name);
	}
	@Override
	public SQLFilter<T> hasCanonicalNameFilter(){
		// all classifications have names
		return new GenericBinaryFilter<T>( true);
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.NameFinder#makeByName(java.lang.String)
	 */
	@Override
	public final T makeFromString(String name) throws DataFault, ParseException{
		if( name == null || name.isEmpty()){
			return null;
		}
		T c = findFromString(name);
		if( c != null ){
			return c;
		}
		
		validateNameFormat(name);
		
		c = makeBDO();
		c.setName(name);
		postMakeByName(c, name);
		c.commit();
		return c;
	}
	/** extension point to allow side effects when a new object is auto-created.
	 * This is called before the commit but after setName
	 * 
	 */
	protected void postMakeByName(T c, String name){
		for(ClassificationCreateContributor<T> ccc : getComposites(ClassificationCreateContributor.class)) {
			ccc.postMakeByName(c, name);
		}
	}
	/** Does the update allow name changes.
	 * defaults to false. Override for tables that are not auto
	 * populated to allow edits.
	 * 
	 * @return boolean
	 */
	protected boolean allowNameChange(){
		return getContext().getBooleanParameter(getConfigTag()+".allow_name_change", false);
	}
	
	protected boolean allowSpacesInName(){
		return getContext().getBooleanParameter(getConfigTag()+".allow_space_in_name", false);
	}
	/**
	 * @author Stephen Booth
	 *
	 */
	public final class ClassificationCodeListInput extends CodeListInput<T> {
		/**
		 * @param fil
		 */
		public ClassificationCodeListInput(BaseFilter<T> fil) {
			super();
			this.fil = fil;
		}

		private final BaseFilter<T> fil;
		@Override
		public T getItembyValue(String value) {
			return findFromString(value);
		}

		@Override
		public Iterator<T> getItems() {
			try {
				return new FilterIterator(fil);
			} catch (DataFault e) {
				getContext().error(e,"Error getting item iterator");
				return null;
			}
		}

		@Override
		public int getCount(){
			try{
				return (int) ClassificationFactory.this.getCount(fil);
			}catch(Exception e){
				getContext().error(e,"Error getting select count");
				return 0;
			}
		}

		@Override
		public String getTagByItem(T item) {
			if( item == null ){
				return null;
			}
			return item.getName();
		}

		@Override
		public String getText(T item) {
			if( item == null){
				return "No item";
			}
			return item.getName();
		}

		@Override
		public boolean isValid(T item) {
			return matches(fil,item);
		}

		
	}
	/** An {@link DataObjectItemInput} that uses the {@link ParseFactory#findFromString(String)} method to locate
	 * 
	 * Note this is not constrained by the select filter so can be used to locate retired objects.
	 * @author spb
	 *
	 */
	public class NameItemInput extends ParseAbstractInput<Integer> implements DataObjectItemParseInput<T>{

		/**
		 * 
		 */
		public NameItemInput() {
			super();
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.inputs.ItemInput#setItem(java.lang.Object)
		 */
		@Override
		public void setItem(T item) {
			if( item == null ){
				setNull();
				return;
			}
			try {
				setValue(item.getID());
			} catch (TypeException e) {
				// should never happend
				throw new TypeError(e);
			}
			
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.inputs.ParseInput#parse(java.lang.String)
		 */
		@Override
		public void parse(String v) throws ParseException {
			if( v == null || v.trim().length()==0){
				setNull();
				return;
			}
			setItem(findFromString(v));
		}
        @Override
		public Integer parseValue(String v) throws ParseException {
        	if( v == null || v.trim().length()==0){
				return null;
			}
			T item = findFromString(v);
			if( item != null) {
				return item.getID();
			}
			return null;
        }
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.data.forms.inputs.DataObjectItemInput#getDataObject()
		 */
		@Override
		public T getItembyValue(Integer val) {
			return find(val);
		}	
	}
	public  class ClassificationCreator extends Creator<T>{

		public ClassificationCreator() {
			super(ClassificationFactory.this);
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.data.DataObjectFormFactory#getSelectors()
		 */
		@Override
		protected Map<String, Selector> getSelectors() {
			Map<String,Selector> result = super.getSelectors();
			
			
			result.put(Classification.NAME, new Selector() {

				@Override
				public Input getInput() {
					// done here as only the create form can check that the name does not exist
					// the update form has to rely on the sql update generating an error.
					UnusedNameInput<T> input = new UnusedNameInput<>(ClassificationFactory.this);
					input.setMaxResultLength(res.getInfo(Classification.NAME).getMax());
					input.setTrim(true);
					return input;
				}
				
			});
			
			// Description is likely to be displayed to user so inhibit html by default
			
			result.put(Classification.DESCRIPTION,new Selector() {

				@Override
				public Input getInput() {
					NoHtmlInput desc_input = new NoHtmlInput();
					if( res.hasField(Classification.DESCRIPTION)){
						desc_input.setMaxResultLength(res.getInfo(Classification.DESCRIPTION).getMax());
					}
					return desc_input;
				}
				
			});
			return result;
		}
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.NameFinder#getFormCreator(uk.ac.ed.epcc.webapp.model.AppUser)
	 */
	@Override
	public FormCreator getFormCreator(AppContext c) {
		return new ClassificationCreator();
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.NameFinder#getDataCache()
	 */
	@Override
	public IndexedDataCache<String,T> getDataCache(boolean auto_create){
		return new IndexedDataCache<String, T>(getContext()){

			@Override
			protected T findIndexed(String key) throws DataException {
				if( auto_create ) {
					try {
						return makeFromString(key);
					} catch (ParseException e) {
						throw new DataException("Invalid name",e);
					}
				}else {
					return findFromString(key);
				}
			}

			@SuppressWarnings("unchecked")
			@Override
			protected IndexedReference<T> getReference(T dat) {
				return new IndexedReference<>(dat.getID(),(Class<? extends IndexedProducer<T>>) ClassificationFactory.this.getClass(),getTag());
			}

			
			
		};
	}
	@Override
	public int compareTo(ClassificationFactory o) {
		return getTag().compareTo(o.getTag());
	}
	
	@Override
	protected List<OrderClause> getOrder() {
		List<OrderClause> order = super.getOrder();
		if( res.hasField(Classification.SORT_ORDER)){
			order.add(res.getOrder(Classification.SORT_ORDER, false));
		}
		order.add(res.getOrder(Classification.NAME, false));
		return order;
	}
	public static class ClassificationUpdater<C extends Classification> extends Updater<C>{

		
		private final boolean allow_name_change;
		protected ClassificationUpdater(ClassificationFactory<C> dataObjectFactory) {
			super(dataObjectFactory);
			allow_name_change= dataObjectFactory.allowNameChange();
		}

		ClassificationFactory<C> getClassificationFactory(){
			return (ClassificationFactory<C>) getFactory();
		}
		@Override
		public void customiseUpdateForm(Form f, C o) {
			// don't allow edits to the name
			super.customiseUpdateForm(f, o);
			if( allow_name_change){
				UnusedNameInput<C> input = new UnusedNameInput<>(getClassificationFactory(),o);
				f.getField(Classification.NAME).setInput(input);
			}else{
				f.getField(Classification.NAME).lock();
			}
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.data.forms.Updater#getSelectInput()
		 */
		@Override
		public DataObjectItemInput<C> getSelectInput() {
			try{
				ClassificationFactory<C> cf = getClassificationFactory();
				BaseFilter<C> finalSelectFilter = cf.getFinalSelectFilter();
				if( cf.getCount(finalSelectFilter) < getContext().getIntegerParameter(cf.getConfigTag()+".max_autocomplete", CLASSIFICATION_MAX_MENU)){

					if( cf.useAutoCompleteInput(finalSelectFilter)) {
						return cf.getInput();
					}
					DataObjectAlternateInput<C, DataObjectItemParseInput<C>> input = new DataObjectAlternateInput<>();
					input.addInput("Menu", "Select ", super.getSelectInput());
					input.addInput("Name", " or specify name ", cf.new NameItemInput());
					return input;
				}
			}catch(Exception e){
				getLogger().error("Error making alt input", e);;
			}
			return getClassificationFactory().new NameItemInput();
		}

		

		
		
	}
	@Override
	public FormUpdate<T> getFormUpdate(AppContext c) {
		return new ClassificationUpdater<>(this);
	}
	
	@Override
	public void customiseForm(Form f) {
		super.customiseForm(f);
		if( ! allowSpacesInName()) {
			f.getField(Classification.NAME).addValidator(new NoSpaceFieldValidator());
		}
		
	}
	/** Get a String valued pull-down input for the classifiers
	 * 
	 * @return
	 */
	@Override
	public final CodeListInput<T> getNameInput(){
		return new ClassificationCodeListInput(getFinalSelectFilter());
	}
	@Override
	public final CodeListInput<T> getNameInput(BaseFilter<T> fil){
		return new ClassificationCodeListInput(fil);
	}
	@Override
	protected Map<String, String> getTranslations() {
		Map<String,String> trans = super.getTranslations();
		if( trans == null ){
			trans =	new HashMap<>();
		}
		trans.put(Classification.SORT_ORDER, "Sort weighting");
		return trans;
	}
	
	@Override
	protected Set<String> getOptional() {
		Set<String> optional = getNullable();
		optional.remove(Classification.NAME);
		return optional;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.ParseFactory#getName(java.lang.Object)
	 */
	@Override
	public String getCanonicalName(T object) {
		return object.getName();
	}
	public final DataObjectItemInput<T> getAutocompleteInput(BaseFilter<T> fil,boolean create,boolean restrict){
		NameFinderInput<T, ClassificationFactory<T>> input = new NameFinderInput<>(this, this,create, restrict, fil);
		return input;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.NameFinder#validateNameFormat(java.lang.String)
	 */
	@Override
	public void validateNameFormat(String name) throws ParseException {
		if( ! allowSpacesInName() && WHITESPACE.matcher(name).find()){
			throw new ParseException("No whitespace allowed");
		}
		if( name.length() > res.getInfo(Classification.NAME).getMax()){
			throw new ParseException("Too long");
		}
	}
	
	public boolean useAutoCompleteInput(BaseFilter<T> fil) {
		if( Feature.checkDynamicFeature(getContext(), getConfigTag()+".use_list_input", false)) {
			return false;
		}
		try {
			return getCount(fil) > getContext().getIntegerParameter(getConfigTag()+".max_pulldown", 100);
		} catch (DataException e) {
			getLogger().error("Error checking option count", e);
			return false;
		}
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.DataObjectFactory#getInput()
	 */
	@Override
	public DataObjectItemInput<T> getInput(BaseFilter<T> fil, boolean restrict) {
		if( useAutoCompleteInput(fil)) {
			return new NameFinderInput<>(this,this, false, restrict, fil);
		}
		return super.getInput(fil,restrict);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.history.HistoryFieldContributor#addToHistorySpecification(uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification)
	 */
	@Override
	public void addToHistorySpecification(TableSpecification spec) {
		// add all the fields to the history specification, except the name since that shouldn't change
		TableSpecification ts = getFinalTableSpecification(getContext(), getTag());
		Set<String> fields = ts.getFieldNames();
		for (String field : fields) {
			if (  getContext().getBooleanParameter(getConfigTag()+".history_field."+field,  !field.equals(Classification.NAME)) ) {
				spec.setField(field, ts.getField(field));
			}
		}
	}
}