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

import java.util.*;
import java.util.regex.Pattern;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.exceptions.InvalidArgument;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
import uk.ac.ed.epcc.webapp.forms.factory.FormCreator;
import uk.ac.ed.epcc.webapp.forms.factory.FormUpdate;
import uk.ac.ed.epcc.webapp.forms.inputs.*;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.*;
import uk.ac.ed.epcc.webapp.jdbc.table.IntegerFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.HistoryFactory;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.filter.SQLValueFilter;
import uk.ac.ed.epcc.webapp.model.data.forms.Creator;
import uk.ac.ed.epcc.webapp.model.data.forms.Updater;
import uk.ac.ed.epcc.webapp.model.data.forms.inputs.DataObjectItemInput;
import uk.ac.ed.epcc.webapp.model.data.forms.inputs.NameFinderInput;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedDataCache;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedProducer;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedReference;
import uk.ac.ed.epcc.webapp.model.history.HistoryFieldContributor;
import uk.ac.ed.epcc.webapp.validation.FieldValidationSet;

/** Factory class for Classification objects.
 * 
 * Table will auto create if it does not exist.
 * @author spb
 * @param <T> 
 *
 */


public class ClassificationFactory<T extends Classification> extends DataObjectFactory<T> implements Comparable<ClassificationFactory>, HistoryFieldContributor, NameFinder<T>,NameInputProvider<T>{
	
	

	static final String CLASSIFICATION_CONFIG_TAG = "Classification";
	
	private static final Pattern WHITESPACE = Pattern.compile("\\s");
	
	private HistoryFactory<T,HistoryFactory.HistoryRecord<T>> hist_fac=null;
	
	public static final String NAME = "Name";

	public static final String DESCRIPTION = "Description";
	/**
	 * 
	 */
	public static final String SORT_ORDER = "SortOrder";
	
	
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
    	TableSpecification spec = ClassificationFactory.getTableSpecification(c,homeTable);
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
    				getLogger().error("Error in classification bootstrap "+table,e);
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
			getLogger().error("Error in findByName",e);
			return null;
		}
	}
	/**
	 * @param name
	 * @return
	 */
	@Override
	public SQLValueFilter<T> getStringFinderFilter(String name) {
		return new SQLValueFilter<>(res,ClassificationFactory.NAME,name);
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
	/** A String valued {@link ItemInput} for {@link Classification}s that generates the classification name. 
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
		public T getItemByTag(String value) {
			return findFromString(value);
		}
		
		@Override
		public Iterator<T> getItems() {
			try {
				return new FilterIterator(fil);
			} catch (DataFault e) {
				getLogger().error("Error getting item iterator",e);
				return null;
			}
		}

		@Override
		public int getCount(){
			try{
				return (int) ClassificationFactory.this.getCount(fil);
			}catch(Exception e){
				getLogger().error("Error getting select count",e);
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
	
	@Override
	protected Map<String, FieldValidationSet> getValidators() {
		Map<String, FieldValidationSet> validators = super.getValidators();
		// Description is likely to be displayed to user so inhibit html by default
		FieldValidationSet.add(validators, DESCRIPTION, new NoHtmlValidator());
		return validators;
	}
	public  class ClassificationCreator extends Creator<T>{

		public ClassificationCreator() {
			super(ClassificationFactory.this);
		}

	
		@Override
		protected Map<String, FieldValidationSet> getValidators() {
			Map<String, FieldValidationSet> validators = super.getValidators();
			// Name should not be in use.
			FieldValidationSet.add(validators, NAME, new UnusedNameValidator<T>(ClassificationFactory.this, null));
			return validators;
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
		if( res.hasField(ClassificationFactory.SORT_ORDER)){
			order.add(res.getOrder(ClassificationFactory.SORT_ORDER, false));
		}
		order.add(res.getOrder(ClassificationFactory.NAME, false));
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
				f.getField(ClassificationFactory.NAME).addValidator(new UnusedNameValidator<C>(getClassificationFactory(), o));
			}else{
				f.getField(ClassificationFactory.NAME).lock();
			}
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.data.forms.Updater#getSelectInput()
		 */
		@Override
		public DataObjectItemInput<C> getSelectInput() {

			// for update we want name inputs so we can access old entries.
			// use auto-complete if sufficiently small
			ClassificationFactory<C> cf = getClassificationFactory();
			BaseFilter<C> finalSelectFilter = cf.getFinalSelectFilter();
			return new NameFinderInput<C, ClassificationFactory<C>>(cf, cf, null, finalSelectFilter);
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
			f.getField(ClassificationFactory.NAME).addValidator(new NoSpaceFieldValidator());
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
	protected Set<String> getOptional() {
		Set<String> optional = getNullable();
		optional.remove(ClassificationFactory.NAME);
		return optional;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.ParseFactory#getName(java.lang.Object)
	 */
	@Override
	public String getCanonicalName(T object) {
		return object.getName();
	}
	public final DataObjectItemInput<T> getAutocompleteInput(BaseFilter<T> fil,boolean create,BaseFilter<T> restrict){
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
		if( name.length() > res.getInfo(ClassificationFactory.NAME).getMax()){
			throw new ParseException("Too long");
		}
	}
	
	
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.DataObjectFactory#getInput()
	 */
	@Override
	public DataObjectItemInput<T> getInput(BaseFilter<T> fil, BaseFilter<T> restrict) {
		
		return new NameFinderInput<>(this,this, restrict, fil);
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
			if (  getContext().getBooleanParameter(getConfigTag()+".history_field."+field,  !field.equals(ClassificationFactory.NAME)) ) {
				spec.setField(field, ts.getField(field));
			}
		}
	}
	/** Generate a default {@link TableSpecification} for a Classification table
	 * 
	 * @param c
	 * @return TableSpecification
	 */
	public static TableSpecification getTableSpecification(AppContext c,String table){
		TableSpecification s = new TableSpecification();
		String prev = s.setCurrentTag(ClassificationFactory.CLASSIFICATION_CONFIG_TAG);
		s.setField(ClassificationFactory.NAME, new StringFieldType(false, null, c.getIntegerParameter(table+".name.length", c.getIntegerParameter("classifier.name.length", 32))));
		if( c.getBooleanParameter(table+".use_description", true)){
			s.setField(ClassificationFactory.DESCRIPTION, new StringFieldType(true, null, c.getIntegerParameter(table+".description.length", c.getIntegerParameter("classifier.description.length", 255))));
		}
		s.setOptionalField(ClassificationFactory.SORT_ORDER, new IntegerFieldType(false, 0));
		try {
			s.new Index("name_key",true,ClassificationFactory.NAME);
		} catch (InvalidArgument e) {
			Logger.getLogger(Classification.class).error("Error making classification key",e);
		}
		s.setCurrentTag(prev);
		return s;
	}
}