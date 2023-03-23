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
package uk.ac.ed.epcc.webapp.model.data;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

import uk.ac.ed.epcc.webapp.*;
import uk.ac.ed.epcc.webapp.content.Labeller;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.forms.FieldValidator;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.forms.factory.*;
import uk.ac.ed.epcc.webapp.forms.inputs.*;
import uk.ac.ed.epcc.webapp.jdbc.DatabaseService;
import uk.ac.ed.epcc.webapp.jdbc.exception.*;
import uk.ac.ed.epcc.webapp.jdbc.filter.*;
import uk.ac.ed.epcc.webapp.jdbc.table.DataBaseHandlerService;
import uk.ac.ed.epcc.webapp.jdbc.table.ReferenceFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.NameFinder;
import uk.ac.ed.epcc.webapp.model.ParseFactory;
import uk.ac.ed.epcc.webapp.model.data.Repository.IdMode;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataNotFoundException;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.MultipleResultException;
import uk.ac.ed.epcc.webapp.model.data.convert.TypeProducer;
import uk.ac.ed.epcc.webapp.model.data.filter.*;
import uk.ac.ed.epcc.webapp.model.data.forms.Creator;
import uk.ac.ed.epcc.webapp.model.data.forms.Selector;
import uk.ac.ed.epcc.webapp.model.data.forms.Updater;
import uk.ac.ed.epcc.webapp.model.data.forms.inputs.DataObjectItemInput;
import uk.ac.ed.epcc.webapp.model.data.forms.inputs.DataObjectItemParseInput;
import uk.ac.ed.epcc.webapp.model.data.iterator.EmptyIterator;
import uk.ac.ed.epcc.webapp.model.data.iterator.SortingIterator;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedProducer;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedReference;
import uk.ac.ed.epcc.webapp.session.SessionService;
import uk.ac.ed.epcc.webapp.timer.TimeClosable;
import uk.ac.ed.epcc.webapp.timer.TimerService;

/**
 * Factory object for producing DataObjects.
 * 
 *  This is an abstract class that is sub-classed to produce
 * a factory for a corresponding sub-class of DataObject.
 * <p>
 * The advantage of using a Factory class rather 
 * than static Factory methods is
 * that we can inherit the methods in a equivalent Factory hierarchy rather than
 * having to re-implement them in the static factory methods of each DataObject.
 * <p>
 * There is a single abstract methods that need to be implemented in the
 * derived Factories:
 * <ul>
 * <li> <code>makeBDO(Record)</code> Which constructs an object of the target type.
 * </ul>
 * Note
 * that Factory is only concerned with retrieving records from the database. It
 * can therefore be used with read-only tables populated from elsewhere.
 * <p>
 * Before a DataObjectFactory can be used the AppContext and database table name needs to be
 * initialised using the <code>setContext</code> method. To avoid the possibility of having invalid
 * instances non-abstract sub-classes should implement constructors that invoke setContext.
 * <p>
 * Normally each class has its own database table. It is possible to implement
 * several tables mapped using the same class by specifying a table name to the
 * Factory constructor. If a class is only to be used with a single hard-wired table the 
 * table name can be set to a constant within the constructor.
 * <p>
 * It is also be possible to implement a class hierarchy that shares a
 * single table with different classes identified by a table field. In this case
 * we use a single factory class that produces the base class of the
 * hierarchy. However the <code>makeBDO(Record)</code> method will have to know about all of the
 * sub-classes so as to be able to call the correct constructor.
 * This works best with classes that are always created from the Factory (e.g. inner classes
 * of the Factory) as it is not possible to have similar behaviour when objects are created 
 * directly from a constructor without passing the table name throughout the code.
 * <p>
 * <code>DataObjectFactory</code> is generic, parameterised by the type of the <code>DataObject</code> sub-class
 * it produces. This reduces the need to introduce casting when using inherited factory methods.
 * Casting may still be required if the Factory can produce multiple target types as the generic parameter will only specify the common
 * superclass.
 * If a sub-classes are going to be extended further they should also be generic:
 <pre>
 <code>
 public class ThingFactory&lt;T extends Thing&gt; extends DataObjectFactory&lt;T&gt;{
    ...
 }
 public class Thing extends DataObject{
     ...
 }
 </code>
 </pre>
 * In this case avoid making the DataObject a true inner class of the Factory. This would make the DataObject
 * generic parameterised by itself and can lead to subtle implementation issues.
 * Instead make is a pseudo-inner class where we add an explicit reference to the Factory (set via the constructor) and define 
 * {@link #makeBDO(uk.ac.ed.epcc.webapp.model.data.Repository.Record)}
 * to set this reference.  
 * <p>
 * DataObjectFactories can be used to retrieve individual 
 * DataObjects (using the various <code>find</code> methods) but
 * are normally used to return sets of DataObjects.
 * These sets are normally specified using a filter (see the interfaces in 
 * <b>uk.ac.ed.epcc.webapp.jdbc.filter</b>). These sets may be very large,
 * too large to all fit in memory at the same time.
 * The {@link FilterIterator} class (an inner class of DataObjectFactory)  
 * is therefore used to iterate through the values.
 * <p>
 * The {@link FilterSet} class is a simple wrapper round a {@link FilterIterator}
 * that implements {@link Iterable} and is therefore more compatible
 * with the java-5 extended for-loop syntax. The {@link #getResult(BaseFilter)} method returns a
 * {@link uk.ac.ed.epcc.webapp.jdbc.filter.FilterSet} corresponding to a filter.
 * <p>
 * This class also implements {@link Selector} which means that it can provide a default form <code>Input</code>
 * for the type of object it creates.
 * <p>
 * A {@link DataObjectFactory} can provide a {@link TableSpecification} that is used to create the database table if the table is missing when the factory is created.
 * This allows code to bootstrap into an empty database.  This behaviour is controlled by the <b>auto_create.tables</b> feature.
 * In general {@link DataObjectFactory} adapts to the existing structure of the database table. Missing fields will be ignored. Additional fields will be handled 
 * using default behaviour. If the table editing transitions are enabled missing fields from the default table specification can be created via the forms.
 * <p>
 * The {@link Composite} class is used to extend {@link DataObjectFactory}s by composition rather than extension. Use this class where the sets of fields in the same table
 * implement unrelated functionality or when a required behaviour has multiple possible implementations.
 * All {@link Composite}s take the factory as a constructor argument. This then calls a register method to register them with the factory.
 * A {@link Composite} may be registered in one of two ways.
 * <ol>
 * <li> A static field in the factory can contain a {@link Composite}. As these are initialised before the {@link AppContext} is set
 * the {@link Composite} constructor is unable to use the {@link AppContext}.</li>
 * <li>A list of {@link Composite} names can be specified in the configuration parameter <b><em>table-tab</em>.composites</b>.
 * These can be either <b>classdef.</b> or <b>class.</b> definitions. If a constructor with the signature
 * <b>(factory, String)</b> exists then the {@link Composite} tag name will be passed to the constructor. In this case the {@link AppContext} is available to 
 * the constructor.
 * @param <BDO> type produced by factory
 */
@SuppressWarnings("javadoc")
public abstract class DataObjectFactory<BDO extends DataObject> implements Tagged, ContextCached, Owner<BDO>, IndexedProducer<BDO>, DataObjectSelector<BDO> , FormCreatorProducer,FormUpdateProducer<BDO>,FilterMatcher<BDO>{
    /**
	 * 
	 */
	public static final String COMPOSITES_SUFFIX = ".composites";

	public static final Feature AUTO_CREATE_TABLES_FEATURE = new Feature("auto_create.tables",false,"attempt to make database tables if they don't exist");

    public static final Feature REJECT_MULTIPLE_RESULT_FEATURE = new Feature("multiple_result.error",true,"Throw an error if a filter find has multiple matching records");

   

	/** A basic form Input for selecting objects using their ID value as text.
     * Only use this where the table is too large to support a pull-down.
     * 
     * To make text forms easier to use the input will attempt to look up the target by name
     * if the parent factory implements {@link NameFinder}.
     * 
     */
    public class DataObjectIntegerInput extends IntegerInput implements DataObjectItemInput<BDO> {

        public DataObjectIntegerInput() {
			super();
			setMin(1);
		}

		@Override
		public BDO getItembyValue(Integer num) {
            if (num == null) {
                // must be optional
                return null;
            }
            try {
                return find(num.intValue());
            } catch (DataException e) {
                return null;
            }
        }

        @Override
		public void setItem(BDO o) {
            if (o == null) {
                setNull();
            } else {
            	
                try {
					setValue(new Integer(o.getID()));
				} catch (TypeException e) {
					throw new TypeError(e);
				}
            }
        }

		@SuppressWarnings("unchecked")
		@Override
		public final Integer convert(Object v) throws TypeException {
			if( v == null ){
				return null;
			}
			if( v instanceof DataObject){
				if( isMine(v)){
					return Integer.valueOf(((BDO)v).getID());
				}else{
					throw new TypeException("DataObject "+v.getClass().getCanonicalName()+" passed to "+getClass().getCanonicalName());
				}
			}
			if( v instanceof IndexedReference ){
				if( isMyReference((IndexedReference) v)){
					return Integer.valueOf(((IndexedReference)v).getID());
				}else{
					throw new TypeException("IndexedReference "+v.toString()+" passed to "+getClass().getCanonicalName());
				}
			}
			return super.convert(v);
		}

		@Override
		public void parse(String v) throws ParseException {
			try{
				super.parse(v);
			}catch(ParseException e){
				if( DataObjectFactory.this instanceof ParseFactory){
					@SuppressWarnings("unchecked")
					BDO value = ((ParseFactory<BDO>)DataObjectFactory.this).findFromString(v);
					if( value != null){
						setItem(value);
						return;
					}
				}
				throw e;
			}

		}
    }
    /** A form Input used to select objects produced by the owning factory.
	 * 
	 * @author spb
	 *
	 */
	public abstract class AbstractDataObjectInput extends DataObjectIntegerInput implements PreSelectInput<Integer,BDO>{
		private BaseFilter<BDO> fil;
		private int max_identifier=DataObject.MAX_IDENTIFIER;
        boolean restrict_parse=true;  // does filter apply to parse as well as offered choice
        boolean allow_pre_select=true;
        private Labeller<? super BDO, String> labeller=null;
		public AbstractDataObjectInput(BaseFilter<BDO> f) {
			this(f,true);
		}
		public AbstractDataObjectInput(BaseFilter<BDO> f,boolean restrict_parse) {
			try{ 
				fil = FilterConverter.convert(f);
			}catch(NoSQLFilterException e){
				fil = f;
			}
			this.restrict_parse = restrict_parse;
			AppContext con = getContext();
			max_identifier = con.getIntegerParameter(getConfigTag()+".maxIdentifier", con.getIntegerParameter("DataObject.MaxIdentifier", DataObject.MAX_IDENTIFIER));
			allow_pre_select = con.getBooleanParameter(getConfigTag()+".allowPreSelect", true);
			addValidator(new FieldValidator<Integer>() {
				
				@Override
				public void validate(Integer num) throws FieldException {
					try {
						
						if (restrict_parse && fil != null ){
							AndFilter<BDO> validate_fil = getValidateFilter(num);
							if( exists(validate_fil)){
								return;
							}



							throw new ValidateException("Invalid input does not match selection filter");
						}else{
							BDO o = getItembyValue(num.intValue());
						}
					} catch (DataNotFoundException e) {
						
						throw new ValidateException("Object does not exist with id "+getTag()+":"+num);
					} catch (DataException e) {
					    getContext().error(e,"Error in DataObjectInput");
					    throw new ValidateException("Internal error", e);
					}
					
				}
			});
		}

		@Override
		public BDO getItembyValue(Integer id) {
			if (id == null || id.intValue() <= 0) {
				// could be optional
				return null;
			}
			if( fil == null || ! restrict_parse){
				return find(id);
			}
			AndFilter<BDO> find_fil = getValidateFilter(id);
			try {
				// force filter to be honoured as well
				return  find(find_fil,true);
			} catch (DataException e) {
				getContext().error(e,"Error in getItemByValue");
				return null;
			}
			
		}

		@Override
		public String getPrettyString(Integer val) {
			// don't apply validation for getPrettyString
			// we want to be able to format an invalid value for 
			String res =  getText(find(val));
			if( res == null ){
				res = "Not Selected";
			}
			return res;
		}
      
		@Override
		public int getCount(){
			try {
				return (int) DataObjectFactory.this.getCount(fil);
			} catch (DataException e) {
				getLogger().error("Error counting items",e);
				return 0;
			}
			
		}
		@Override
		public Iterator<BDO> getItems() {
			try {
				return new FilterIterator(fil);
			} catch (DataFault e) {
				getLogger().error("Error making select Iterator",e);
				return new EmptyIterator<>();
			}
		}
  
		

		@Override
		public String getTagByValue(Integer id) {
			return id.toString();
		}

		@Override
		public String getText(BDO obj) {
			if( obj == null ){
				return null;
			}
			if( labeller != null ) {
				return labeller.getLabel(getContext(), obj);
			}
			String result = obj.getIdentifier(max_identifier);
			
			if ( result != null && result.length() > max_identifier) {
				result = result.substring(0, max_identifier);
			}
			if( result == null || result.trim().length() == 0 ){
				return "Un-named object "+obj.getID();
			}
			return result;
		}
		
		/** 
		 * @param num
		 * @return
		 */
		private AndFilter<BDO> getValidateFilter(Number num) {
			return getAndFilter(fil, 
					getMatchFilter(num.intValue()));
		}
		/** get the Value from an Item
		 * 
		 * @param item
		 * @return
		 */
		public abstract Integer getValueFromItem(BDO item);
		
		@Override
		public String getTagByItem(BDO item) {
			return Integer.toString(getValueFromItem(item));
		}
		/** The filter that selects records by input id
		 * 
		 * @param value
		 * @return
		 */
		protected abstract BaseFilter<BDO> getMatchFilter(int value);
		@Override
		public <R> R accept(InputVisitor<R> vis) throws Exception {
			return vis.visitListInput(this);
		}
		@Override
		public boolean allowPreSelect() {
			return allow_pre_select;
		}
		@Override
		public void setPreSelect(boolean value) {
			allow_pre_select=value;
			
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.forms.inputs.ListInput#isValid(java.lang.Object)
		 */
		@Override
		public boolean isValid(BDO item) {
			if(item == null) {
				return false;
			}
			try {
				return exists(getValidateFilter(item.getID()));
			} catch (DataException e) {
				return false;
			}
		}
		public int getMaxIdentifier() {
			return max_identifier;
		}
		public void setMaxIdentifier(int max_identifier) {
			this.max_identifier = max_identifier;
		}
		public Labeller<? super BDO, String> getLabeller() {
			return labeller;
		}
		public void setLabeller(Labeller<? super BDO, String> labeller) {
			this.labeller = labeller;
		}
	}
    /** A form Input used to select objects produced by the owning factory using the record id
	 * 
	 * @author spb
	 *
	 */
	public class DataObjectInput extends AbstractDataObjectInput {

		/**
		 * @param f
		 * @param restrict_parse
		 */
		public DataObjectInput(BaseFilter<BDO> f, boolean restrict_parse) {
			super(f, restrict_parse);
		}

		/**
		 * @param f
		 */
		public DataObjectInput(BaseFilter<BDO> f) {
			super(f);
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.data.DataObjectFactory.AbstractDataObjectInput#getMatchFilter(int)
		 */
		@Override
		protected BaseFilter<BDO> getMatchFilter(int value) {
			return new SQLIdFilter<>( res, value);
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.data.DataObjectFactory.AbstractDataObjectInput#getValueFromItem(uk.ac.ed.epcc.webapp.model.data.DataObject)
		 */
		@Override
		public Integer getValueFromItem(BDO item) {
			return item.getID();
		}
		
	}

	public class SortingDataObjectInput extends DataObjectInput implements DataObjectItemParseInput<BDO>{
		 Comparator<? super BDO> comp;
		public SortingDataObjectInput( BaseFilter<BDO> f,Comparator<? super BDO> comp) {
			super(f);
			this.comp=comp;
		}
		/** construct SortingDataObjectInput
		 * default to sorting by order of getIdentifier output
		 * 
		 * @param f
		 */
		public SortingDataObjectInput(BaseFilter<BDO> f){
			super(f);
			comp = new Comparator<BDO>(){

				@Override
				public int compare(BDO o1, BDO o2) {
					return o1.getIdentifier().compareTo(o2.getIdentifier());
				}
				
			};
		}
		public SortingDataObjectInput( BaseFilter<BDO> f,boolean restrict_parse,Comparator<? super BDO> comp) {
			super(f,restrict_parse);
			this.comp=comp;
		}
		@Override
		public Iterator<BDO> getItems() {
			if( comp == null ){
			  return super.getItems();
			}else{
				return new SortingIterator<>(super.getItems(),comp);
			}
		}
		
	}

	/** A {@link ResultMapper} for generating target {@link DataObject}s
	 * 
	 * @author spb
	 *
	 */
    public class FilterAdapter implements ResultMapper<BDO>{
 
    	public FilterAdapter(){
    	}
    	boolean qualify = false;
		@Override
		public BDO makeObject(ResultSet rs) throws DataException {
				   return  DataObjectFactory.this.makeObject(rs,qualify);
		}

		@Override
		public String getTarget() {
			if( qualify ){ 
				final StringBuilder sb = new StringBuilder();
				// joins can give multiple results where the joined values 
				// differ. As we are selecting the full table this will always
				// give the desired result but its probably not efficient.
				if( DatabaseService.USE_SQL_DISTICT_FEATURE.isEnabled(getContext())){
					sb.append(" DISTINCT ");
				}
				return res.addAlias(sb, true).append(".*").toString();
			}else{
				return "*";
			}
		}

		@Override
		public String getModify() {
			return null;
		}

		@Override
		public BDO makeDefault() {
			return null;
		}
		@Override
		public boolean setQualify(boolean qualify) {
			boolean old = this.qualify;
			this.qualify = qualify;
			return old;
		}

		@Override
		public SQLFilter getRequiredFilter() {
			return null;
		}

		@Override
		public List<PatternArgument> getTargetParameters(
				List<PatternArgument> list) {
			return list;
		}

		@Override
		public List<PatternArgument> getModifyParameters(
				List<PatternArgument> list) {
			return list;
		}
		@Override
		public String toString() {
			return "FilterAdapter [ factory="+DataObjectFactory.this.getClass().getSimpleName()+" tag="+DataObjectFactory.this.getTag()+"]";
		}
    }
   
    public class FilterExists extends AbstractFinder<Boolean>{
		public FilterExists() {
			super();
			setMapper(new ExistsMapper());
		}
    }
    protected abstract class AbstractFinder<X> extends FilterFinder<BDO, X>{
		public AbstractFinder(boolean allow_null) {
			super(DataObjectFactory.this.getContext(),DataObjectFactory.this.getTag(),allow_null);
		}
		
		public AbstractFinder() {
			super(DataObjectFactory.this.getContext(),DataObjectFactory.this.getTag());
		}
		@Override
		protected final void addSource(StringBuilder sb) {
			res.addSource(sb, true);
			
		}

		@Override
		protected final Set<Repository> getSourceTables() {
			HashSet<Repository> set = new HashSet<>();
			set.add(res);
			return set;
		}

		@Override
		protected final String getDBTag() {
			return res.getDBTag();
		}
    }
    public class FilterCounter extends AbstractFinder<Long>{
		public FilterCounter() {
			super();
			setMapper(new CounterMapper());
		}

		
    	
    }
	/**
	 * Iterate over BasicDataObjects generated by a filter.
	 * 
	 * This is intended to return the results of a Database query. Returning an
	 * Iterator is usually preferable to returning an array of objects,
	 * especially when only a single pass over the results is required.
	
	 * 
	 * @author spb
	 * 
	 */
	public class FilterIterator extends ResultIterator<BDO> {
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.SQLResultIterator#fallbackOrder()
		 */
		@Override
		protected String fallbackOrder() {
			return OrderBy(getQualify());
		}

		/** protected constructor to allow sub-classes to initialise fields before
		 * calling setup
		 * 
		 *
		 */
		protected FilterIterator(){
	    	super(DataObjectFactory.this.getContext(),DataObjectFactory.this.getTag());
	    	setMapper(new FilterAdapter());
	    }
		
	    public FilterIterator(BaseFilter<BDO> fil) throws DataFault{
	    	this();
	    	try {
				setup(fil,0,-1);
			} catch (DataException e) {
				throw new DataFault("Error in setup", e);
			}
	    }
	    /**
		 * Initialise using a filter. A null filter selects the entire table.
		 * This allows the sequence to be limited to a subset. This constructor
		 * only applies to SQLFilters as the accept method would further reduce
		 * the number of objects returned
		 * 
		 * @param f
		 *            SQLFilter to specify selection
		 * @param start
		 *            position in sequence to start (according to the condition
		 *            clause)
		 * @param max
		 *            maximum number of records to return.
	     * @throws DataFault 
		 * @throws DataFault
		 */
	    public FilterIterator(SQLFilter<BDO> f, int start, int max) throws DataFault{
	    	this();
	    	try {
				setup(f,start,max);
			} catch (DataException e) {
				throw new DataFault("Error in setup", e);
			}
	    }

		@Override
		protected final void addSource(StringBuilder sb) {
			res.addSource(sb, true);
			
		}

		@Override
		protected final Set<Repository> getSourceTables() {
			HashSet<Repository> set = new HashSet<>();
			set.add(res);
			return set;
		}

		@Override
		protected final String getDBTag() {
			return res.getDBTag();
		}
	   
	}
	
	public class Finder extends AbstractFinder<BDO>{
        public Finder(){
        	this(false);
        }
		public Finder(boolean allow_null) {
			super(allow_null);
			// Don't care about order if multiple results is an error
			setMapper(new FilterAdapter());
		}
	}
	
	/** Simple wrapper round a Filter that implements  {@link Iterable}
	 * This makes it easy to re-create a {@link FilterIterator} or to
	 * use a Filter in a for-each loop.
	 * 
	 * There is also a convenience method to generate a {@link Collection} from
	 * the iterator if we want to hold the complete collection in memory
	 * @author spb
	 *
	 */
	protected class FilterSet extends AbstractFilterResult<BDO> implements Iterable<BDO>, FilterResult<BDO>{
		private BaseFilter<BDO> f;
       
        
       
        int start;
        int max;
        public FilterSet(BaseFilter<BDO> f) throws DataFault{
        	
        	try {
				this.f = FilterConverter.convert(f);
			} catch (NoSQLFilterException e) {
				this.f=f;
			}
        	start=-1;
        	max=-1;
        }
        public FilterSet(SQLFilter<BDO> f, int start, int max) throws DataFault{
        	this.f=f;
        	this.start=start;
        	this.max=max;
        }
		
		@Override
		protected CloseableIterator<BDO> makeIterator() throws DataFault {
			if( start < 0 ){
				return new FilterIterator(f);
			}else{
				return new FilterIterator((SQLFilter<BDO>)f, start,max);
			}
		}
		
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.data.AbstractFilterResult#getLogger()
		 */
		@Override
		protected Logger getLogger() {
			return DataObjectFactory.this.getLogger();
		}
		@Override
		public boolean isEmpty() throws  DataFault{
			try {
				return !exists(f);
			}catch(DataFault df) {
				throw df;
			} catch (DataException e) {
				throw new DataFault("Error in isEmpty",e);
			}
		}		
      
	}
	
	
	
	/** An {@link AcceptFilter} version of DestFilter
	 * 
	 * @author spb
	 * @param <T> type of filter
	 */


	public class DestAcceptFilter<T extends DataObject> implements AcceptFilter<T>{
		private final BaseFilter<BDO> fil;
		private final String join_field;
		private final DataObjectFactory<T> join_fac;
		public DestAcceptFilter(BaseFilter<BDO> fil, String join_field, DataObjectFactory<T> join_fac){
			this.fil=fil;
			this.join_field=join_field;
			this.join_fac=join_fac;
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.AcceptFilter#accept(java.lang.Object)
		 */
		@Override
		public boolean test(T o) {
			AndFilter<BDO> and = getAndFilter(
					fil,
					new ReferenceFilter<>(DataObjectFactory.this, join_field, o));
			try {
				return exists(and);
			} catch (DataException e) {
				getLogger().error("Error checking filter",e);
				return false;
			}
		}
	}
	/** Filter to select Objects that match against a field of a peer object
	 * 
	 * @author spb
	 *
	 */
	protected class MatchFilter extends SQLValueFilter<BDO>{

		public MatchFilter(String field, MatchCondition cond,
				BDO peer) {
			super(res, field, cond, peer.record.getProperty(field));
		}
		public MatchFilter( String field, BDO peer,
				boolean negate) {
			super(res, field, peer.record.getProperty(field), negate);
		}
		public MatchFilter(String field,BDO peer){
			super(res,field,peer.record.getProperty(field));
		}
	}
	protected  class TimeFilter extends SQLValueFilter<BDO>{
		public TimeFilter(String field, MatchCondition cond, Date point){
        	super(res,field,cond,point);
        }
	}
	
	
	public static class  TimeAcceptFilter<T extends DataObject> implements AcceptFilter<T>{
	
        private final String field;
        private final MatchCondition cond;
        private final Date point;
		public TimeAcceptFilter(String tag,String field, MatchCondition cond, Date point){
        	this.field=field;
        	this.cond=cond;
        	this.point=point;
        }
		@Override
		public boolean test(T d) {
			Date t = d.record.getDateProperty(field);
			switch(cond){
			case LT: return t.before(point); 
			case LE: return ! t.after(point);
			case GT: return t.after(point);
			case GE: return ! t.before(point);
			case NE: return ! t.equals(point);
			}
			return false;
		}
	}
	private AppContext conn=null;
	// This should match the repository tag but
	// we store a copy here so we can make getTag and
	// getConfigTag work before the repository is constructed
	private String tag=null; 
	protected Repository res=null;
    private Finder finder=null;
    private Map<Class,Composite<BDO,?>> composites;
    /** Construct an uninitialised DataObjectFactory
     * The object will not be usable until setContext is called.
     * This is protected becasue Normally sub-classes should not expose this constructor unless they
     * are multi table classes and we want to set the table after construction for some reason.
     * non abstract Sub-classes should produce public constructors which call setContext to initialise the 
     * factory.
     */
    protected DataObjectFactory(){
    	composites = new LinkedHashMap<>();
    }

    /** register a {@link Composite} with this factory. 
     * This is actually called inside the {@link Composite} constructor so the object may not be fully constructed at this point.
     * However its ok to take a reference.
     * 
     * 
     * @param c
     */
    final void registerComposite(Composite c){
    	Class type = c.getType();
    	if( type == null) {
    		getLogger().error("Composite "+c.getClass().getCanonicalName()+" registered as null type");
    	}else if( ! type.isAssignableFrom(c.getClass())) {
    		getLogger().error("Composite "+c.getClass().getCanonicalName()+" registered under incompatible type "+type.getCanonicalName());
    	}else {
    		composites.put(type, c);
    		observeComposite(c);
    	}
    }
    /** Observer {@link Composite}s as they are registered.
     * 
     * Note that as this can be called during factory construction no assumptions can be made about
     * factory attribute state. The {@link Composite#preRegister()} method needs to be used to set attribute state that is going to be used by this method.
     * 
     * @param c
     */
    protected void observeComposite(Composite c){
    	
    }
    /** get all {@link TableStructureContributer}s for this factory.
     * 
     * This includes the {@link Composite}s by default but can be extended to add others.
     * 
     * @return {@link Collection}
     */
    public Collection<TableStructureContributer<BDO>> getTableStructureContributers(){
    	return new LinkedList<>(getComposites());
    }
    /** get all {@link Composite}s for this factory.
     * @return {@link Collection}
     */
	public Collection<Composite<BDO,?>> getComposites() {
		return composites.values();
	}
    /** get a specific {@link Composite} based on its  registration type.
     * 
     * @param clazz prototype {@link Composite} is registered under
     * @return {@link Composite}
     */
	@SuppressWarnings("unchecked")
	public <X extends Composite> X getComposite(Class<? super X> clazz){
		X found = (X) composites.get(clazz);
		assert( found == null || checkComposite(clazz));
		return found;
	}
	
	public <X extends Composite> boolean checkComposite(Class<? super X> clazz) {
		if( clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
			return true; // can't check
		}
		AppContext conn = getContext();
		if( conn.findConstructorFromParamSignature(clazz, getClass()) != null) {
			return true;
		}
		if( conn.findConstructorFromParamSignature(clazz, getClass(),String.class) != null) {
			return true;
		}
		return false;
	}
	
	public <X extends Composite> boolean hasComposite(Class<X> clazz){
		return composites.containsKey(clazz);
	}
	/** Get all composites that are assignable to a particular type.
	 * 
	 * @param template
	 * @return
	 */
	public <Y> Collection<Y> getComposites(Class<Y> template){
		LinkedList<Y> result = new LinkedList<>();
		for( Composite<BDO,?> c : composites.values()){
			if( template.isAssignableFrom(c.getClass()) ){
				result.add((Y) c);
				
			}
		}
		return result;
	}
	/** Apply an action to all {@link Composite}s matching a template
	 * 
	 * @param <Y>
	 * @param template
	 * @param action
	 */
	public <Y> void visitComposites(Class<Y> template,Consumer<Y> action) {
		for( Composite<BDO,?> c : composites.values()){
			if( template.isAssignableFrom(c.getClass()) ){
				action.accept((Y) c);				
			}
		}
	}
	
	/** use like a destructor. clears internal references
	 * 
	 */
	public void release(){
		// make sure we are not cached before destroying state
		getContext().removeCached(null,getTag());
		res=null;
		finder=null;
		if( composites != null ){
			for(Composite c : composites.values()) {
				c.release();
			}
			composites.clear();
		}
		composites=null;
	}
	
    @Override
	public FormCreator getFormCreator(AppContext c){
    	return new Creator<>(this);
    }
    @Override
	public FormUpdate<BDO> getFormUpdate(AppContext c){
    	return new Updater<>(this);
    }
    /** Make a filter from an integer reference
     * @see SelfReferenceFilter
     * @param id
     * @return
     */
    public SQLFilter<BDO> getFindFilter(int id){
    	return new SelfReferenceFilter<>( res, makeReference(id));
    }
    /** Get a {@link SQLFilter} that excludes  a certain object
     * 
     * @param obj
     * @return
     */
    public SQLFilter<BDO> getExcludeFilter(BDO obj){
    	return new SelfReferenceFilter<>( res,true, makeReference(obj));
    }

	/**
	 * Extension hook to allow additional Form customisation generic to all
	 * types of Form For example adding a FormValidator or adding min, max
	 * values to NumberInputs.
	 * 
	 * @param f
	 *            Form to modify
	 */
	public  void customiseForm(Form f) {

	}


	
	/**
	 * Get a single object via its unique id.
	 * 
	 * @param id
	 *            Id of the object
	 * @return The required object or null if id was null.
	 * @throws uk.ac.ed.epcc.webapp.jdbc.exception.DataException
	 * 
	 */
	@Override
	public  BDO find(int id)
			throws uk.ac.ed.epcc.webapp.jdbc.exception.DataException {
		Repository.Record rec = res.new Record();
		rec.setID(id);
		// set the ID before making the object in case this is
		// a multi-class table.
		BDO	dat = (BDO) makeBDO(rec);		

		return dat;
	}
	
	/**
	 * get a single Object via its unique id as a number. If the number is null
	 * then return null
	 * 
	 * @param id
	 *            Number identifying object or null
	 * @return matching DataObject from this factory or null if id was null
	 */
	public final BDO find(Integer id) {
		return find((Number) id);
	}
	@Override
	public final BDO find(Number id) {
		if (id == null || id.intValue()==0) {
			return null;
		}
		try {
			return find(id.intValue());
		}catch(DataNotFoundException e1){
			// not necessarily an error
			return null;
		} catch (DataException e) {
			getContext().error(e,"Error finding BDO");
			return null;
		}
	}
	@Override
	public Integer getIndex(BDO value) {
		if( value == null ) {
			return null;
		}
		return value.getID();
	}


	
	
	/** find an object based on a filter
	 * 
	 * @param f
	 * @return
	 * @throws DataException
	 */
    public BDO find(BaseFilter<BDO> f) throws DataException{
    	return find(f,false);
    }
    /** Find an object based on a Filter
     * 
     * @param f {@link BaseFilter} to select object
     * @param allow_null  return null if not found
     * @return selected DataObject
     * @throws DataException
     */
    public BDO find(BaseFilter<BDO> f,boolean allow_null) throws DataException{
    	try{
    		SQLFilter<BDO> sql_fil = FilterConverter.convert(f);
    		if( finder == null){
    			finder=new Finder();
    		}
    		return finder.find(sql_fil,allow_null);
    	}catch(NoSQLFilterException e){
    		try(FilterIterator it = new FilterIterator(f)){
    			if( it.hasNext()){
    				BDO result = it.next();
    				if( it.hasNext() ){
    					if( DataObjectFactory.REJECT_MULTIPLE_RESULT_FEATURE.isEnabled(getContext())){

    						throw new MultipleResultException("Found multiple "+getTag()+" records expecting 1");
    					}else{
    						// just log
    						getContext().getService(LoggerService.class).getLogger(getClass()).error("Multiple "+getTag()+" records expecting 1",new Exception());
    					}
    				}
    				return result;
    			}else{
    				if( allow_null ){
    					return null;
    				}
    				throw new DataNotFoundException("No result from filter");
    			}
    		}
    	}
    	
    }
/** count the number of records selected by a Filter
 * 
 * @param s filter to use
 * @return long
 * @throws DataException 
 */
	public final long getCount(BaseFilter<BDO> s) throws DataException{
		try{
			SQLFilter<BDO> sql_fil = FilterConverter.convert(s);
			FilterCounter counter = new FilterCounter();
			return ((Long)counter.find(sql_fil)).longValue();
		}catch(NoSQLFilterException e){
			// do things the hard way
			long count=0;
			try(CloseableIterator<BDO> it = new FilterIterator(s)){
				while(it.hasNext()){
					count++;
					it.next();
				}
			} catch (Exception e1) {
				throw new DataException("Error in close", e1);
			}
			return count;
		}
	}
	public final boolean exists(BaseFilter<BDO> s) throws DataException{
		try{
			SQLFilter<BDO> sql_fil = FilterConverter.convert(s);
			FilterExists counter = new FilterExists();
			return ((Boolean)counter.find(sql_fil)).booleanValue();
		}catch(NoSQLFilterException e){
			// do things the hard way

			try(TimeClosable t = new TimeClosable(getContext(), () -> "exists-iterator."+getTag());
					FilterIterator  it = new FilterIterator(s)){
				return it.hasNext();
			}

		}
	}
	// Note this is used to implement FilterMatcher so
	// we can't pass ourselves as the matcher arg
	private final ConvertPureAcceptFilterVisitor<BDO> accept_converter = new ConvertPureAcceptFilterVisitor<>(null);
	@Override
	public final boolean matches(BaseFilter<? super BDO> fil, BDO o) {
		if( fil == null || o == null){
			return false;
		}
		try {
			// Use AcceptFilter by preference.
			AcceptFilter<BDO> accept = (AcceptFilter<BDO>) ((BaseFilter)fil).acceptVisitor(accept_converter);
			if( accept != null){
				return accept.test(o);
			}
		} catch (Exception e1) {
			getLogger().error("Error converting to AcceptFilter", e1);
		}
		// Have to do a SQL query
		@SuppressWarnings("unchecked")
		AndFilter<BDO> and = getAndFilter(fil, getFilter(o) );
		try {
			return exists(and);
		} catch (DataException e) {
			getLogger().error("Error checking for filter match", e);
			return false;
		}
	}


	/**
	 * get an Iterator over all contents of a table
	 * 
	 * @return Iterator
	 * @throws DataFault
	 */
	public final CloseableIterator<BDO> getAllIterator() throws DataFault {
		return new FilterIterator(null);
	}
	/** Get an Iterable over all contents of a table
	 * 
	 * @return Iterable
	 * @throws DataFault 
	 */
	public final FilterResult<BDO> all() throws DataFault{
		return new FilterSet(null);
	}

	

	@Override
	final public AppContext getContext() {
		return conn;
	}

	/**
	 * Generate a set of default property values. override this in sub-classes
	 * to give defaults when creating objects.
	 * 
	 * @return hashtable of default properties
	 */
	protected Map<String, Object> getDefaults() {
		HashMap<String, Object> defaults = new HashMap<>();
		for(TableStructureContributer<BDO> t : getTableStructureContributers()){
			t.addDefaults(defaults);
		}
		return defaults;
	}

	

	/** Should the default input restrict parse values based on
	 * {@link #getSelectFilter()}. Normally the default input
	 * is more relaxed and accepts other values because the default input is 
	 * usually only based on status not access control rules.
	 * 
	 * @return
	 */
	protected boolean restrictDefaultInput(){
		return false;
	}

	public final BaseFilter<BDO> getFinalSelectFilter(){
		AndFilter<BDO> fil = getAndFilter();
		fil.addFilter(getSelectFilter());
		for(SelectModifier mod : getComposites(SelectModifier.class)){
			fil.addFilter(mod.getSelectFilter());
		}
		return fil;
	}
	/**
	 * get the default {@link Input} for this Factory
	 * Default behaviour is to use the {@link #getSelectFilter()} method to generate a {@link DataObjectInput}
	 * but <em>not</em> to restrict the parse values (default value of {@link #restrictDefaultInput()} so existing values that don't
	 * match the select filter are still valid.
	 */
	@Override
	public DataObjectItemInput<BDO> getInput() {
		return getInput(getFinalSelectFilter(),restrictDefaultInput());
	}
	
	/** Create an {@link Input} from a filter.
	 * This will restrict valid results as well as offered choices.
	 * 
	 * @param fil
	 * @return {@link DataObjectItemInput}
	 */
	public final DataObjectItemInput<BDO> getInput(BaseFilter<BDO> fil){
		return getInput(fil,true);
	}
	/** Generate the default input type. This is usually a {@link ListInput}
	 * but can be overidden to return an auto-complete input.
	 * 
	 * Note that the error message when a 
	 * 
	 * @param fil   {@link BaseFilter} for suggested values
	 * @param restrict boolean should filter restrict results
	 * @return
	 */
	public DataObjectItemInput<BDO> getInput(BaseFilter<BDO> fil,boolean restrict){
		return new DataObjectInput(fil,restrict);
	}
	public class FilterSelector implements DataObjectSelector<BDO>{
		private final boolean restrict;
		public FilterSelector(BaseFilter<BDO> fil,boolean restrict) {
			super();
			this.fil = fil;
			this.restrict=restrict;
		}

		private final BaseFilter<BDO> fil;

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.data.forms.Selector#getInput()
		 */
		@Override
		public DataObjectItemInput<BDO> getInput() {
			return DataObjectFactory.this.getInput(fil,restrict);
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.model.data.DataObjectSelector#getSelector(uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter)
		 */
		@Override
		public DataObjectSelector<BDO> narrowSelector(BaseFilter<BDO> filter) {
			return new FilterSelector(getAndFilter(fil, filter), restrict);
		}
		@Override
		public DataObjectSelector<BDO> narrowSelector(BaseFilter<BDO> filter,boolean new_restrict) {
			return new FilterSelector(getAndFilter(fil, filter), new_restrict);
		}
	}
	/** create a {@link Selector} from a filter.
	 * 
	 * Input will be restricted to the filter
	 * @param fil {@link BaseFilter} for selection
	 * @return {@link Selector}
	 */
	public final DataObjectSelector<BDO> getSelector(BaseFilter<BDO> fil){
		return new FilterSelector(fil,true);
	}
	
	@Override
	public final DataObjectSelector<BDO> narrowSelector(BaseFilter<BDO> fil){
		// This should narrow the default selector of this class
		return new FilterSelector(getAndFilter(getFinalSelectFilter(),fil),restrictDefaultInput());
	}
	@Override
	public final DataObjectSelector<BDO> narrowSelector(BaseFilter<BDO> fil, boolean new_restrict){
		// This should narrow the default selector of this class
		return new FilterSelector(getAndFilter(getFinalSelectFilter(),fil),new_restrict);
	}
	/** create a {@link Selector} from a filter.
	 * 
	 * @param fil      {@link BaseFilter} for selection
	 * @param restrict  should validation fail if filter does not match
	 * @return {@link Selector}
	 */
	public final Selector<DataObjectItemInput<BDO>> getSelector(BaseFilter<BDO> fil,boolean restrict){
		return new FilterSelector(fil,restrict);
	}
	/** Create a {@link FilterResult} from a filter
	 * 
	 * @param fil {@link BaseFilter} to select object set.
	 * @return {@link FilterResult}
	 * @throws DataFault
	 */
	public final FilterResult<BDO> getResult(BaseFilter<BDO> fil) throws DataFault{
		return new FilterSet(fil);
	}
	
	/** Create a {@link FilterResult} from a filter
	 * 
	 * @param fil {@link SQLFilter} to select object set.
	 * @param start
	 * @param max
	 * @return {@link FilterResult}
	 * @throws DataFault
	 */
	public final FilterResult<BDO> getResult(SQLFilter<BDO> fil,int start, int max) throws DataFault{
		return new FilterSet(fil,start,max);
	}
	/** Get  a filter corresponding to the set of DataObjects that can be used in
	 * the default Input for this class. Note that if getInput is overridden then 
	 * this filter condition may not be applied.
	 * This filter is responsible for providing any ORDER BY clause in
	 * the filter. The default implementation in DataObjectFactory will
	 * generate an OrderFilter based on  the factory OrderBy method. 
	 * Note that {@link #getInput()} only uses this to control the presented options not
	 * which options are valid unless the value of {@link #restrictDefaultInput()} is also changed.
	 * @return
	 */
    public BaseFilter<BDO> getSelectFilter(){
    	return getDefaultOrderFilter();
    }

    /** Get a {@link SQLOrderFilter} corresponding to the
     * factory default ordering.
     * 
     * @return
     */
	protected SQLOrderFilter<BDO> getDefaultOrderFilter() {
		// By default just supply the default ordering if there is one.
    	String order = OrderBy(false);
    	Logger log = getLogger();
    	log.debug("selectfilter get order by of "+order);
    	if( order != null && order.trim().length() > 0){
    		return new SQLOrderFilter<BDO>() {

			
				@Override
				public List<OrderClause> OrderBy() {
					return DataObjectFactory.this.getOrder();
				}

				@Override
				public <X> X acceptVisitor(FilterVisitor<X, BDO> vis)
						throws Exception {
					return vis.visitOrderFilter(this);
				}

				@Override
				public String getTag() {
					return DataObjectFactory.this.getTag();
				}

				
				
			};
    	}
    	return null;
	}
    /** Get a {@link SQLFilter} to use as the default target for relationship filters.
     * This should exclude targets that should not be used as relationship targets such as retired objects.
     * 
     * @return
     */
    public SQLFilter<BDO> getDefaultRelationshipFilter(){
    	BaseFilter<BDO> fil = getFinalSelectFilter();
    	try {
			return FilterConverter.convert(fil);
		} catch (NoSQLFilterException e) {
			return null;
		}
    }
    /** get the default {@link Logger}
     * 
     * @return {@link Logger}
     */
	protected Logger getLogger() {
		return getContext().getService(LoggerService.class).getLogger(getClass());
	}

	/** get the set of fields that can be null in the database.
	 * 
	 * 
	 * @return Set<String>
	 */
	protected final Set<String> getNullable() {
		Set<String> nullable = new HashSet<>();
		for (String name : res.getFields()) {
			Repository.FieldInfo info = res.getInfo(name);
			if (info.getNullable()) {
				nullable.add(name);
			}
		}
		return nullable;
	}
	/**
	 * generate the set of optional fields to be used to provide class specific defaults
	 *  in form creation/update.
	 * If null is returned the default behaviour is to take fields that can be null in the database but
	 * classes can override with an explicit list of optional fields. In which case it may be a good idea to
	 * start with {@link #getNullable()} and remove any fields that should be forced to be mandatory.
	 * Alternatively you can start with an empty set (defaulting to all fields mandatory) and add exceptions
	 * 
	 * Note that for legacy reasons a non-nullable String field <b>CAN</b> be made optional (the {@link Repository}
	 * will map null values to the empty string) 
	 * @return Vector
	 * @see #getNullable()
	 * @See {@link DataObjectFormFactory}
	 */
	protected Set<String> getOptional() {
		return null;
	}

	
	/** Get a {@link ReferenceFieldType} for this factory.
	 * 
	 * @return ReferenceFieldType
	 */
	public ReferenceFieldType getReferenceFieldType(){
		return new ReferenceFieldType(getTag());
	}
	/** Get a {@link ReferenceFieldType} for this factory.
	 * @param allow_null
	 * @return ReferenceFieldType
	 */
	public ReferenceFieldType getReferenceFieldType(boolean allow_null){
		return new ReferenceFieldType(allow_null,getTag());
	}

	/**
	 * Get a Map of selectors to use for forms of this type.
	 * 
	 * This method provides a class specific set of defaults but the specific form classes can
	 * override this.
	 * @return Map
	 * @see DataObjectFormFactory
	 */
	protected Map<String,Selector> getSelectors() {

		return new HashMap<>();
	}
	/**
	 * generate the class specific set of suppressed fields to be used in form creation/update
	 * The individual forms can override these so you usually use this method to define fields that should
	 * be suppressed in <em>all</em> forms.
	 * 
	 * @return Set<String>
	 * @see DataObjectFormFactory
	 */
	protected Set<String> getSupress() {
		return new HashSet<>();
	}
	
	/** get the {@link FieldConstraint}s to apply in creation/update forms
	 * 
	 * @return
	 */
	protected Map<String,FieldConstraint> getFieldConstraints(){
		return new HashMap<String, FieldConstraint>();
	}
	/** Generate the default text identifier of the client object.
	 * This is used in urls and html inputs and defaults to the integer
	 * representation of the object.
	 * If this method is overridden the factory must
	 * implement {@link ParseFactory} in a complementary way.
	 * 
	 * @param obj
	 * @return
	 */
	@Override
	public String getID(BDO obj){
		return Integer.toString(obj.getID());
	}

	/** Return the identifying tag for this instance. 
	 * the tag is used to construct property names and 
	 * is the tag to construct the corresponding Repository
	 * 
	 * 
	 * This is usually the table name but should not be used in SQL statements.
	 *  
	 * @return String
	 */
	@Override
	public final String getTag(){
		if( res == null) {
			return tag;
		}
		return res.getTag();
	}
	/** Return the tag used to qualify configuration parameters
	 * 
	 * @return string tag
	 */
	public final String getConfigTag(){
		if( res == null ) {
			return conn.getInitParameter(Repository.CONFIG_TAG_PREFIX+tag,tag);
		}
		return res.getParamTag();
	}
	/**
	 * return a default set of translation between field names and text labels.
	 * This method provides a class specific set of defaults. The individual Form classes can still override this.
	 * 
	 * @return Map<String,String>
	 * @see DataObjectFormFactory
	 */
	protected Map<String, String> getTranslations() {
		// default to no translations override this method in sub-classes
		return new HashMap<>();
	}

	/**
	 * return a class-default set of help-text for form fields.
	 * 
	 * 
	 * @return Hashtable
	 */
	protected Map<String, String> getFieldHelp() {
		// default to no translations override this method in sub-classes
		return new HashMap<>();
	}
	// name of table index
	/**
	 * Get the unique ID field name for the target object.
	 * 
	 * @return String
	 */
	protected final String getUniqueIdName() {
		return res.getUniqueIdName();
	}

	/**
	 * Check a Object to see if it is a DataObject from the same Repository as
	 * used by this Factory.
	 * 
	 * @param o
	 *            DataObject to check
	 * @return boolean true if object belongs
	 */
	public final boolean isMine(DataObject o) {
		if( o == null){
			return false;
		}
		return getTag().equals(o.getFactoryTag());
	}

	@Override
	public final boolean isMine(Object ob) {
		if (ob instanceof DataObject) {
			return isMine((DataObject) ob);
		}
		return false;
	}
	/** Check that this is a valid DataObjectFactory connected to a valid database table
	 * 
	 * @return true if factory valid
	 */
    public boolean isValid(){
    	try{
    		if( res == null ){
    			return false;
    		}
    		Set<String> fields = res.getFields();
    	   return fields != null && fields.size() > 0;
    	}catch(NoTableException nt) {
    		return false;
    	}catch(Exception e){
    		getLogger().error("Error in getFields", e);
    		throw new DataError(e);
    	}
    }
	/**
	 * Create a new DataObject of the correct Class
	 * 
	 * @return DataObject
	 * @throws DataFault
	 */
	@SuppressWarnings("unchecked")
	public final BDO makeBDO() throws DataFault {
		Repository.Record record = makeRecord();
		BDO result = (BDO) makeBDO(record);
		return result;
	}

	/**  Create an initial record for the embedded repository
	 * 
	 * @return
	 */
	protected final Repository.Record makeRecord() {
		Repository.Record record = res.new Record();
		record.putAll(getDefaults());
		return record;
	}

	/** create a record with a specified id. 
	 * USed when unpacking a data dump where we want to preserve ids.
	 * 
	 * @param id
	 * @return
	 * @throws DataException
	 */
	protected final Repository.Record makeRecordForImport(int id) throws DataException{
		Repository.Record r = makeRecord();
		r.setID(id, IdMode.UseExistingIfPresent);
		return r;
	}
	/**
	 * Construct a new Blank DataObject of the correct Class
	 * This method takes a Record as an argument. This allows the factory to either create 
	 * different sub-classes depending on a value in the record or to produce objects from
	 * different Repositories/tables.

	 *  We keep the return type as DataObject instead of making it generic as
	 * this helps with legacy compatibility a little bit (problem is if we have
	 * another generic sub-type between us and a legacy sub-class

	 * @param res
	 *            Record to create object from
	 * 
	 * 

	 * @return new object of correct class.
	 * @throws DataFault
	 */
	protected abstract BDO makeBDO(Repository.Record res) throws DataFault;

	/**
	 * generate the DataObject from the ResultSet as part of a join
	 * 
	 * @param rs
	
	 * @return DataObject
	 * @throws DataException 
	 */

       @SuppressWarnings("unchecked")
	protected BDO makeObject(ResultSet rs,boolean qualify) throws DataException {
                Repository.Record record = res.new Record();
		record.setContents(rs,qualify);
		BDO o =  (BDO) makeBDO(record);
		return o;
	}
   
	@Override
	public IndexedReference<BDO> makeReference(BDO obj){
		if( obj == null ){
			return makeReference(0); // Null refs are allowed
		}
		if( ! isMine(obj)){
			throw new ConsistencyError("Reference taken for wrong type/table "+getTag());
		}
    	return makeReference(obj.getID());
    }
	 @Override
	@SuppressWarnings("unchecked")
    public IndexedReference<BDO> makeReference(int id){
    	Class<? extends IndexedProducer<BDO>> c = (Class<? extends IndexedProducer<BDO>>) getClass();
    	IndexedReference<BDO> ref = new IndexedReference<>(id,c,getTag());
		return ref;
    }
    @Override
	public boolean isMyReference(IndexedReference ref){
    	if( ref == null || ref.isNull()){
    		return false;
    	}
    	Class factoryClass = ref.getFactoryClass();
		if( factoryClass != null &&  getClass() != factoryClass){
    		return false;
    	}
    	if( ref.getTag() != null && ! ref.getTag().equals(getTag())){
    		return false;
    	}
    	return true;
    }
    /** update table schema to add any missing fields from the table specification
     * 
     * @throws DataFault
     */
    public void updateTable() throws DataFault{
    	DataBaseHandlerService serv = getContext().getService(DataBaseHandlerService.class);
    	TableSpecification spec = getFinalTableSpecification(getContext(), getTag());
    	String prefix ="create_table."+getTag()+".";
    	spec.setFromParameters(conn,prefix, conn.getInitParameters(prefix));
		serv.updateTable(res, spec);
    }
	/**
	 * method to return a default ORDER by clause used. This can be overridden
	 * by sub-classes. Needs to include the SQL keywords if defined
	 * 
	 * @return SQL order by clause.
	 */
	protected final String OrderBy(boolean qualify) {
		StringBuilder sb = new StringBuilder();
		sb.append(" ORDER BY ");
		List<OrderClause> list = getOrder();
		if( list == null || list.size()==0){
			// if nothing specified we want to define something. Otherwise results will
			// depend on DB implementation. Tests are happier with some default
			res.addUniqueName(sb, qualify, true);
		}else{

			boolean seen=false;
			for(OrderClause o : list){
				if( seen ){
					sb.append(", ");
				}
				o.addClause(sb, qualify);
				seen=true;
			}
		}
		return sb.toString();
	}

	protected List<OrderClause> getOrder(){
		return new LinkedList<>();
	}


	/** Initialise Repository
	 * Call this from within a sub-class constructor.
	 * 
	 * @param ctx
	 * @param homeTable
	 */

	protected final void setContext(AppContext ctx, String homeTable) {
	   setContext(ctx, homeTable, AUTO_CREATE_TABLES_FEATURE.isEnabled(ctx));
	}
	protected void postSetContext() {
		
	}
	protected final boolean setContext(AppContext ctx, String homeTable,boolean create) {
		if (res != null  ){
			getLogger().debug("Attempt to reset Repository");
			return false;
		}
		if (homeTable == null) {
			throw new ConsistencyError("No table specified");
		}
		
		TimerService timer = ctx.getService(TimerService.class);
		if( timer != null ){ timer.startTimer("setContext"); timer.startTimer("setContext:"+homeTable);}
		try{
		// This sets the AppContext if not already set.
		setComposites(ctx, homeTable);
		
		res = Repository.getInstance(ctx, homeTable);
		if( create){
			if( ! isValid()){
				// Make sure we don't attempt this more than once per context
				// Reference fields should trigger creation of targets and
				// we need to worry about mutual cross references
				String create_tag = "auto_create_"+homeTable;
				if( ctx.getAttribute(create_tag) != null){
					return false;
				}
				ctx.setAttribute(create_tag, Boolean.TRUE);
				TableSpecification spec = getFinalTableSpecification(ctx,
						homeTable);
				if( spec != null ){
					try {
						if( ctx.getService(DatabaseService.class).getSQLContext().isReadOnly()) {
							throw new DataError("Cannot create table, read-only connection");
						}
					} catch (SQLException e) {
						throw new FatalDataError("Cannot retreive SQLContext", e);
					}
					if( timer != null ){ timer.startTimer("makeTable"); timer.startTimer("makeTable:"+homeTable);}
					try{
						return makeTable(ctx, homeTable, spec);
					}finally{
						if( timer != null ){ timer.stopTimer("makeTable"); timer.stopTimer("makeTable:"+homeTable);}
					}
				}
			}
		}
		}finally{
			if( timer != null ){ timer.stopTimer("setContext:"+homeTable); timer.stopTimer("setContext");}
			postSetContext();
		}
		return false;

	}

	/** Set {@link Composite}s 
	 * This is called early in the construction process so should not assume fields are initialised
	 * 
	 * This method can be extended to initialise sub-class specific compositions.
	 * @param ctx
	 * @param homeTable
	 */
	protected void setComposites(AppContext ctx, String homeTable) {
		if( conn != null ) {
			// Only call setComposites once
			return;
		}
		// set local reference so composites can 
		// use the getContext and getConfigTag methods
		// in their constructors.
		this.conn = ctx;
		this.tag = homeTable;
		String composite_list = ctx.getExpandedProperty(homeTable+COMPOSITES_SUFFIX);
		// can't use getLogger as context not set yet
		Logger logger = ctx.getService(LoggerService.class).getLogger(getClass());
		
		if( composite_list != null && composite_list.trim().length() > 0){
			for(String comp : composite_list.split("\\s*,\\s*")){
				Class<? extends Composite> clazz = null;
				clazz = ctx.getPropertyClass(Composite.class,null , comp);
				if( clazz == null ){ 
					clazz = ctx.getClassDef(Composite.class, comp);
				}
				if( clazz != null){
					try {
						// Try two possible constructors
						Constructor c = ctx.findConstructor(clazz, this, comp);
						if( c != null ){
							Composite cc = (Composite) c.newInstance(this, comp);
						}else{
							c = ctx.findConstructor(clazz, this);
							if( c != null ){
								Composite cc = (Composite) c.newInstance(this);
							}else{
								logger.error("Composite "+comp+" "+clazz.getCanonicalName()+" does not implement any of the required constructor signatures");
							}
						}
					} catch (Exception e) {
						
						logger.error("Cannot make composite "+comp, e);
					}
				}else{
					logger.error("Unrecognised composite tag "+comp);
				}
			}
		}
	}

	/**
	 * @param ctx
	 * @param homeTable
	 * @return
	 */
	public final TableSpecification getFinalTableSpecification(AppContext ctx,
			String homeTable) {
		TableSpecification spec = getDefaultTableSpecification(ctx, homeTable);
		if( spec != null ){
			for(TableStructureContributer c : getTableStructureContributers()){
				spec = c.modifyDefaultTableSpecification(spec, homeTable);
			}
		}
		return spec;
	}
	
	/** Get the default {@link TableSpecification} from a fully constructed factory.
	 * 
	 * This can only be called after auto-table generation.
	 * @return
	 */
	public TableSpecification getTableSpecification() {
		if( conn == null) {
			throw new ConsistencyError("TableSepcification requested without context");
		}
		return getFinalTableSpecification(getContext(), getTag());
	}
	/** Get the default table specification if the table is to be created.
	 * A null result means that a default specification is not available and the table will not be created.
	 * @param c AppContext
	 * @param table String table name.
	 * @return null or TableSpecification
	 */
	protected TableSpecification getDefaultTableSpecification(AppContext c, String table){
		return null;
	}
	/** Initialise Repository with customised table creation.
	 * This is for use where the table specification depends on the constructor parameters,
	 * or if we want to override the auto_create.tables feature
	 * 
	 * @param ctx
	 * @param homeTable
	 * @param spec
	 * @return true if table created
	 */
	protected final boolean setContextWithMake(AppContext ctx, String homeTable,TableSpecification spec) {
		setContext(ctx, homeTable,false);
		if( ! isValid() && (spec != null)){
			return makeTable(ctx, homeTable, spec);
		}
		return false;
	}
	private boolean makeTable(AppContext ctx, String homeTable,TableSpecification spec){
		DataBaseHandlerService dbh = ctx.getService(DataBaseHandlerService.class);
		if( dbh != null ){
			try{
				spec.setPrimaryKey(homeTable+"RecordID");
				// Create the table name repository tried to access before.
				dbh.createTable(Repository.tagToTable(ctx, homeTable), spec);
			    // repository should re-try to get metadata if failed previously
				assert(isValid());
				postCreateTableSetup(ctx,homeTable);
				return true;
			}catch(DataFault e){
				ctx.error(e,"Error making table "+homeTable);
			}
		}
		return false;
	}
   
	protected void postCreateTableSetup(AppContext c, String table){
		
	}
	/** Access method to allow factory sub-classes to access the {@link Repository.Record} of their {@link DataObject}s
	 * 
	 * @param o
	 * @return
	 */
	protected Repository.Record getRecord(BDO o) {
		assert(isMine(o));
		return o.record;
	}
	/** get a filter that selects a particular target object.
     * 
     * @param target
     * @return
     */
    public SQLFilter<BDO> getFilter(BDO target){
    	return getFilter(target, false);
    }
    /** get a filter that selects/excludes a particular target object.
     * 
     * @param target
     * @return
     */
    public SQLFilter<BDO> getFilter(BDO target,boolean exclude){
    	if( target == null){
    		throw new ConsistencyError("null target in getFilter");
    	}
    	if( ! isMine(target)){
    		throw new ConsistencyError("unexpected target "+target.getFactoryTag());
    	}
    	return new SQLIdFilter<>( res, target.getID(),exclude);
    }


	@Override
	public String toString() {
		return getClass().getCanonicalName()+"["+getTag()+"]";
	}


	@Override
	public final int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((res == null) ? 0 : res.hashCode());
		return result;
	}


	@Override
	public final boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataObjectFactory other = (DataObjectFactory) obj;
		if (res == null) {
			if (other.res != null)
				return false;
		} else if (!res.equals(other.res))
			return false;
		return true;
	}


	/** Convert a BDO {@link BaseFilter} into a {@link BaseFilter} on a referenced factory.
	 * 
	 * @param fil
	 * @return
	 */
	protected <T extends DataObject> BaseFilter<T> convertToDestinationFilter(DataObjectFactory<T> remote_fac, String link_field,BaseFilter<BDO> fil){
		try{
			SQLFilter<BDO> sqlfilter = FilterConverter.convert(fil);
			if( sqlfilter == null ) {
				return null;
			}
			return getDestFilter(sqlfilter, link_field,remote_fac);
		}catch(NoSQLFilterException e){
			return new DestAcceptFilter<>(fil, link_field, remote_fac);
		}
		
	}
	/** A {@link FilterVisitor} for making a local filter from a filter on a referenced table.
	 * 
	 * @author spb
	 *
	 * @param <R>
	 */
	public class MakeRemoteFilterVisitor<R extends DataObject> implements FilterVisitor<BaseFilter<BDO>, R>{
		private final DataObjectFactory<R> remote_fac;
		private final String field;
		
		public MakeRemoteFilterVisitor(DataObjectFactory<R> remote_fac,String field){
			this.remote_fac=remote_fac;
			this.field=field;
		}
		/** default mechanism
		 * if the filter can be converted to a pure {@link SQLFilter} use that
		 * otherwise a {@link RemoteAcceptFilter}
		 * 
		 * @param fil
		 * @return
		 */
		public BaseFilter<BDO> visitBaseFilter(BaseFilter<R> fil){
			try{
				SQLFilter<R> sqlfilter = FilterConverter.convert(fil);
				return visitSQLFilter(sqlfilter);
			}catch(NoSQLFilterException e){
				return new RemoteAcceptFilter<>(remote_fac, field, fil);
			}
		}
		public SQLFilter<BDO> visitSQLFilter(SQLFilter<R> fil){
			return getRemoteSQLFilter(remote_fac, field, fil);
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitPatternFilter(uk.ac.ed.epcc.webapp.jdbc.filter.PatternFilter)
		 */
		@Override
		public BaseFilter<BDO> visitPatternFilter(PatternFilter<R> fil) throws Exception {
			return visitSQLFilter((SQLFilter<R>) fil);
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitSQLCombineFilter(uk.ac.ed.epcc.webapp.jdbc.filter.BaseSQLCombineFilter)
		 */
		@Override
		public BaseFilter<BDO> visitSQLCombineFilter(BaseSQLCombineFilter<R> fil) throws Exception {
			return visitSQLFilter((SQLFilter<R>) fil);
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitAndFilter(uk.ac.ed.epcc.webapp.jdbc.filter.AndFilter)
		 */
		@Override
		public BaseFilter<BDO> visitAndFilter(AndFilter<R> fil) throws Exception {
			try{
				if( ! fil.hasAcceptFilters()){
					SQLFilter<R> sqlfilter = FilterConverter.convert(fil);
					return getRemoteSQLFilter(remote_fac, field, sqlfilter);
				}
			}catch(NoSQLFilterException e){
			}
			BaseFilter<BDO> result =new RemoteAcceptFilter<>(remote_fac, field, fil);
			if( fil.hasPatternFilters() ){
				result = getAndFilter(result, getRemoteSQLFilter(remote_fac,field,fil.getNarrowingFilter()));
			}
			return result;

		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitOrFilter(uk.ac.ed.epcc.webapp.jdbc.filter.OrFilter)
		 */
		@Override
		public BaseFilter<BDO> visitOrFilter(OrFilter<R> fil) throws Exception {
			return visitBaseFilter(fil);
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitOrderFilter(uk.ac.ed.epcc.webapp.jdbc.filter.OrderFilter)
		 */
		@Override
		public BaseFilter<BDO> visitOrderFilter(SQLOrderFilter<R> fil) throws Exception {
			return visitSQLFilter((SQLFilter<R>) fil);
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitAcceptFilter(uk.ac.ed.epcc.webapp.jdbc.filter.AcceptFilter)
		 */
		@Override
		public BaseFilter<BDO> visitAcceptFilter(AcceptFilter<R> fil) throws Exception {
	
			return new RemoteAcceptFilter<>(remote_fac, field, fil);
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitJoinFilter(uk.ac.ed.epcc.webapp.jdbc.filter.JoinFilter)
		 */
		@Override
		public BaseFilter<BDO> visitJoinFilter(JoinFilter<R> fil) throws Exception {
			return visitSQLFilter((SQLFilter<R>) fil);
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitBinaryFilter(uk.ac.ed.epcc.webapp.jdbc.filter.BinaryFilter)
		 */
		@Override
		public BaseFilter<BDO> visitBinaryFilter(BinaryFilter<R> fil) throws Exception {
			// A remote binary filter can become a local binary filter
			return new GenericBinaryFilter<>( fil.getBooleanResult());
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitDualFilter(uk.ac.ed.epcc.webapp.jdbc.filter.DualFilter)
		 */
		@Override
		public BaseFilter<BDO> visitDualFilter(DualFilter<R> fil) throws Exception {
			return visitBaseFilter(fil);
		}
		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.jdbc.filter.FilterVisitor#visitBinaryAcceptFilter(uk.ac.ed.epcc.webapp.jdbc.filter.BinaryAcceptFilter)
		 */
		@Override
		public BaseFilter<BDO> visitBinaryAcceptFilter(BinaryAcceptFilter<R> fil) throws Exception {
			return visitBinaryFilter(fil);
		}
		
	}
	/** Create a BDO {@link BaseFilter} from a {@link BaseFilter} on a referenced factory.
	 * 
	 * If you have more than one field that references the same table and want to combine remote filters for them
	 * register the remote factory under multiple tags (one per reference field) and set table_alias.tag properties 
	 * to specify aliases to use in the SQL.
	 * @param fil
	 * @return
	 */
	public <R extends DataObject> BaseFilter<BDO> getRemoteFilter(DataObjectFactory<R> remote_fac, String link_field,BaseFilter<R> fil){
		if( fil == null ){
			return null;
		}
		try {
			return fil.acceptVisitor(new MakeRemoteFilterVisitor<>(remote_fac, link_field));
		} catch (Exception e1) {
			getLogger().error("Impossible error", e1);
			return null;
		}
		
	}
	public <R extends DataObject> SQLFilter<BDO> getRemoteSQLFilter(DataObjectFactory<R> remote_fac, String link_field,SQLFilter<R> fil){
		return Joiner.getRemoteFilter( fil, link_field, res, remote_fac.res);
	}
	/** Default filter for use when we want to select remote objects referenced from this table
	 * with a select clause on this table.
	 * 
	 * @param fil {@link SQLFilter} on local object
	 * @param join_field field pointing to remote object
	 * @param join_fac {@link DataObjectFactory} for remote object.
	 */
	public <T extends DataObject> SQLFilter<T> getDestFilter(SQLFilter<BDO> fil, String join_field, DataObjectFactory<T> join_fac){
		return Joiner.getDestFilter(fil, join_field,join_fac.res,res);
	}

	/** convert a {@link Predicate} into an {@link AcceptFilter} 
	 * 
	 * In general {@link SQLFilter}s are preferable as they filter at the SQL level.
	 * However this gives an easy way of adding additional restrictions to a selection using lambdas.
	 * These should really only be used as a final level of filtering as many of the query optimisations will be prevented by the presence 
	 * of an {@link AcceptFilter}
	 * 
	 * 
	 * @param p
	 * @return
	 */
	public final AcceptFilter<BDO> getPredicateFilter(Predicate<BDO> p){
		return (o) -> p.test(o);
	}
	/** create a {@link AndFilter} for this factory
	 * 
	 * @param filters
	 * @return
	 */
	@SafeVarargs
	public final AndFilter<BDO> getAndFilter(BaseFilter<? super BDO> ... filters){
		return new AndFilter<>(getTag(),filters);
	}
	/** create a {@link SQLAndFilter} for this factory
	 * 
	 * @param filters
	 * @return
	 */
	@SafeVarargs
	public final SQLAndFilter<BDO> getSQLAndFilter(SQLFilter<? super BDO> ... filters){
		return new SQLAndFilter<>(getTag(),filters);
	}
	/** create a {@link OrFilter} for this factory
	 * 
	 * @param filters
	 * @return
	 */
	@SafeVarargs
	public final OrFilter<BDO> getOrFilter(BaseFilter<? super BDO> ... filters){
		return new OrFilter<>(getTag(), this, filters);
	}
	
	/** create a {@link SQLOrFilter} for this factory
	 * 
	 * @param filters
	 * @return
	 */
	@SafeVarargs
	public final SQLOrFilter<BDO> getSQLOrFilter(SQLFilter<? super BDO> ... filters){
		return new SQLOrFilter<>(getTag(), filters);
	}
}