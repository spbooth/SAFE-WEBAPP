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

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
import uk.ac.ed.epcc.webapp.forms.factory.FormCreator;
import uk.ac.ed.epcc.webapp.forms.factory.FormUpdate;
import uk.ac.ed.epcc.webapp.forms.inputs.CodeListInput;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.inputs.NameInputProvider;
import uk.ac.ed.epcc.webapp.forms.inputs.ParseAbstractInput;
import uk.ac.ed.epcc.webapp.forms.inputs.UnusedNameInput;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.AndFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.OrderClause;
import uk.ac.ed.epcc.webapp.jdbc.table.AbstractTableRegistry;
import uk.ac.ed.epcc.webapp.jdbc.table.GeneralTransitionSource;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecificationTransitionSource;
import uk.ac.ed.epcc.webapp.jdbc.table.TableTransitionRegistry;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.filter.SQLIdFilter;
import uk.ac.ed.epcc.webapp.model.data.filter.SQLValueFilter;
import uk.ac.ed.epcc.webapp.model.data.forms.Creator;
import uk.ac.ed.epcc.webapp.model.data.forms.Updater;
import uk.ac.ed.epcc.webapp.model.data.forms.inputs.DataObjectAlternateInput;
import uk.ac.ed.epcc.webapp.model.data.forms.inputs.DataObjectItemInput;
import uk.ac.ed.epcc.webapp.model.data.forms.inputs.DataObjectItemParseInput;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedDataCache;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedProducer;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedReference;
import uk.ac.ed.epcc.webapp.model.data.table.TableStructureDataObjectFactory;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** Factory class for Classification objects.
 * 
 * Table will auto create if it does not exist.
 * @author spb
 * @param <T> 
 *
 */


public class ClassificationFactory<T extends Classification> extends TableStructureDataObjectFactory<T> implements Comparable<ClassificationFactory>, NameFinder<T>,NameInputProvider{
	/** Maximum size of pull-down menu in update form.
	 * 
	 */
	private static final int CLASSIFICATION_MAX_MENU = 200;
    public ClassificationFactory(AppContext ctx, String homeTable){
    	setContext(ctx, homeTable);
    }
    @Override
    public TableSpecification getDefaultTableSpecification(AppContext c,String homeTable){
      TableSpecification spec = Classification.getTableSpecification(c,homeTable);
	return spec;	
    }
    
	public class ClassificationTableRegistry extends AbstractTableRegistry{

		public ClassificationTableRegistry(){
			TableSpecification spec = getDefaultTableSpecification(getContext(), getTag());
			if(spec != null ){
				addTransitionSource(new TableSpecificationTransitionSource<ClassificationFactory>(res, spec));
			}
			addTransitionSource(new GeneralTransitionSource<ClassificationFactory<T>>(res));
		}

		
		public void getTableTransitionSummary(ContentBuilder hb,
				SessionService operator) {
			hb.addText("Classification table");
		}
		
	}
	protected TableTransitionRegistry makeTableRegistry(){
		return new ClassificationTableRegistry();
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
	protected DataObject makeBDO(Record res) throws DataFault {
		return new Classification(res);
	}
	@Override
	public Class<? super T> getTarget() {
		return Classification.class;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.NameFinder#findByName(java.lang.String)
	 */
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
	public SQLValueFilter<T> getStringFinderFilter(String name) {
		return new SQLValueFilter<T>(getTarget(),res,Classification.NAME,name);
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.NameFinder#makeByName(java.lang.String)
	 */
	public T makeFromString(String name) throws DataFault{
		if( name == null ){
			return null;
		}
		T c = findFromString(name);
		if( c != null ){
			return c;
		}
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
		
	}
	/** Does the update allow name changes.
	 * defaults to false. Override for tables that are not auto
	 * populated so allow edits.
	 * 
	 * @return boolean
	 */
	protected boolean allowNameChange(){
		return false;
	}
	/** An {@link DataObjectItemInput} that uses the {@link ParseFactory#findFromString(String)} method to locate
	 * 
	 * Note this is not constrained by the select filter so can be used to locate retired obects.
	 * @author spb
	 *
	 */
	public class NameItemInput extends ParseAbstractInput<Integer> implements DataObjectItemParseInput<T>{

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.inputs.ItemInput#getItem()
		 */
		@Override
		public T getItem() {
			return getDataObject();
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.inputs.ItemInput#setItem(java.lang.Object)
		 */
		@Override
		public void setItem(T item) {
			if( item == null ){
				setValue(null);
				return;
			}
			setValue(item.getID());
			
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.inputs.ParseInput#parse(java.lang.String)
		 */
		@Override
		public void parse(String v) throws ParseException {
			if( v == null || v.trim().length()==0){
				setValue(null);
				return;
			}
			setItem(findFromString(v));
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.data.forms.inputs.DataObjectItemInput#getDataObject()
		 */
		@Override
		public T getDataObject() {
			return find(getValue());
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
		protected Map<String, Object> getSelectors() {
			Map<String,Object> result = super.getSelectors();
			// done here as only the create form can check that the name does not exist
			// the update form has to rely on the sql update generating an error.
			UnusedNameInput<T> input = new UnusedNameInput<T>(ClassificationFactory.this);
			input.setMaxResultLength(res.getInfo(Classification.NAME).getMax());
			result.put(Classification.NAME, input);
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
	public IndexedDataCache<String,T> getDataCache(){
		return new IndexedDataCache<String, T>(getContext()){

			@Override
			protected T findIndexed(String key) throws DataException {
				return makeFromString(key);
			}

			@SuppressWarnings("unchecked")
			@Override
			protected IndexedReference<T> getReference(T dat) {
				return new IndexedReference<T>(dat.getID(),(Class<? extends IndexedProducer<T>>) ClassificationFactory.this.getClass(),getTag());
			}

			
			
		};
	}
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
				f.getField(Classification.NAME).setInput(new UnusedNameInput<C>(getClassificationFactory(),o));
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
				if( getClassificationFactory().getCount(getClassificationFactory().getSelectFilter()) < CLASSIFICATION_MAX_MENU){


					DataObjectAlternateInput<C, DataObjectItemParseInput<C>> input = new DataObjectAlternateInput<C, DataObjectItemParseInput<C>>();
					input.addInput("Menu", "Select ", super.getSelectInput());
					input.addInput("Name", " or specify name ", getClassificationFactory().new NameItemInput());
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
		return new ClassificationUpdater<T>(this);
	}
	
	/** Get a String valued pull-down input for the classifiers
	 * 
	 * @return
	 */
	public final Input<String> getNameInput(){
		return new CodeListInput<T>(){

			public T getItembyValue(String value) {
				return findFromString(value);
			}

			public Iterator<T> getItems() {
				try {
					return new FilterIterator(getSelectFilter());
				} catch (DataFault e) {
					getContext().error(e,"Error getting item iterator");
					return null;
				}
			}
			public int getCount(){
				try{
					return (int) ClassificationFactory.this.getCount(getSelectFilter());
				}catch(Exception e){
					getContext().error(e,"Error getting select count");
					return 0;
				}
			}

			public String getTagByItem(T item) {
				if( item == null ){
					return null;
				}
				return item.getName();
			}

			public String getText(T item) {
				if( item == null){
					return "No item";
				}
				return item.getName();
			}

			@Override
			public boolean isValid(T item) {
				
				try {
					return exists(new AndFilter<T>(getTarget(), getSelectFilter(),new SQLIdFilter<T>(getTarget(), res, item.getID())));
				} catch (DataException e) {
					return false;
				}
			}

			
			
		};
	}
	@Override
	protected Map<String, String> getTranslations() {
		Map<String,String> trans = super.getTranslations();
		if( trans == null ){
			trans =	new HashMap<String, String>();
		}
		trans.put(Classification.SORT_ORDER, "Sort weighting");
		return trans;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.ParseFactory#getName(java.lang.Object)
	 */
	@Override
	public String getCanonicalName(T object) {
		return object.getName();
	}
	
	
}