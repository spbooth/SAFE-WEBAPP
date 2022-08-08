package uk.ac.ed.epcc.webapp.model.data.forms.inputs;

import java.util.LinkedHashSet;
import java.util.Set;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.FieldValidator;
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.forms.inputs.AutoComplete;
import uk.ac.ed.epcc.webapp.forms.inputs.ParseAbstractInput;
import uk.ac.ed.epcc.webapp.forms.inputs.TypeError;
import uk.ac.ed.epcc.webapp.forms.inputs.TypeException;
import uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.NameFinder;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedReference;

/** An {@link DataObjectItemInput} for {@link NameFinder} factories.
 * 
 * This is a text {@link AutoComplete} input using the permitted names
 * optionally creating bootstrap entries 
 * 
 * Autocompletion can be 
 * 
 * @author spb
 *
 */
public class NameFinderInput<T extends DataObject,F extends DataObjectFactory<T>> extends ParseAbstractInput<Integer> implements AutoComplete<T, Integer>, DataObjectItemInput<T>{
	/**
	 * 
	 */
	protected final F factory;
	protected final NameFinder<T> finder; // in most cases this will aslo be the factory but not always
	
	private String match_error = null;
	/** create input
	 * 
	 * @param create   make entry if not found
	 * @param restrict  restrict with filter
	 * @param autocomplete  suggestions/restrict filter
	 * @param factory {@link DataObjectFactory} and {@link NameFinder}
	 */
	public NameFinderInput(F factory, NameFinder<T> finder,boolean create, boolean restrict,BaseFilter<T> autocomplete) {
		this(factory,finder,create,true,restrict,autocomplete);
	}
	/** create input
	 * 
	 * @param create   make entry if not found
	 * @param use_autocomplete   use autocomplete input or a simple text input
	 * @param restrict  restrict with filter
	 * @param autocomplete  suggestions/restrict filter
	 * @param factory {@link DataObjectFactory} and {@link NameFinder}
	 */
	public NameFinderInput(F factory, NameFinder<T> finder,boolean create, boolean use_autocomplete, boolean restrict,BaseFilter<T> autocomplete) {

		super();
		this.factory = factory;
		this.finder = finder;
		this.create = create;
		this.autocomplete = autocomplete;
		this.restrict=restrict;
		this.use_autocomplete = use_autocomplete;
		addValidator(new FieldValidator<Integer>() {
			
			@Override
			public void validate(Integer data) throws FieldException {
				T item = getItembyValue(data);
				if( item == null) {
					throw new ValidateException("Value does not correspond to item");
				}
				if( restrict){
					
					if( ! factory.matches(autocomplete, item)){
						if( match_error != null) {
							throw new ValidateException(match_error);
						}
						throw new ValidateException("Input does not match required filter");
					}
				}
			}
		});
	}
	private boolean create;
	private final boolean restrict;
	private boolean use_autocomplete;  // allow autocomplete to be turned off while keeping the filter for restrictions.
	private final BaseFilter<T> autocomplete;
	
	public T parseItem(String v) throws ParseException {
		if( v == null || v.trim().isEmpty()) {
			return null;
		}
		try{
			boolean created = false;
			T target=finder.findFromString(v);
			if( target == null && create ){
				finder.validateNameFormat(v);
				target=finder.makeFromString(v);
				created=true;
			}
			if(target == null) {
				throw new ParseException("["+v+"] Not found");
			}
			// validate against the filter unless we created a new object
			// a newly created object is unlikely to match the filter but
			// probably will as a result of the operation.
			// we assume if creating a new object is requested then
			// any newly created object is a valid result
			if( restrict && autocomplete != null && ! created) {
				if( ! factory.matches(autocomplete, target)) {
					throw new ParseException("["+v+"] Not valid");
				}
			}
			return target;
		}catch(ParseException p) {
			throw p;
		}catch(Exception e){
			throw new ParseException(e);
		}
	}
	@Override
	public void parse(String v) throws ParseException {
		setItem(parseItem(v));
	}
	@Override
	public Integer parseValue(String v) throws ParseException {
		T item = parseItem(v);
		if( item == null) {
			return null;
		}
		return item.getID();
	}
	@Override
	public T getItembyValue(Integer value) {
		return factory.find(value);
	}
	@Override
	public void setItem(T item) {
		if( item == null){
			setNull();
		}else{
			try {
				setValue(item.getID());
			} catch (TypeException e) {
				throw new TypeError(e);
			}
		}
	}
	@Override
	public Set<T> getSuggestions() {
		LinkedHashSet<T> result = new LinkedHashSet<>();
		if( autocomplete != null){
			try {
				this.factory.getResult(autocomplete).toCollection(result);
			} catch (DataFault e) {
				getLogger().error("Error getting suggestions",e);
			}
		}
		return result;
		
	}
	
	public AppContext getContext(){
		return factory.getContext();
	}
	protected Logger getLogger(){
		return getContext().getService(LoggerService.class).getLogger(getClass());
	}
	@Override
	public String getValue(T item) {
		return finder.getCanonicalName(item);
	}
	@Override
	public String getSuggestionText(T item) {
		return getValue(item)+": "+item.getIdentifier();
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.AbstractInput#getString(java.lang.Object)
	 */
	@Override
	public String getString(Integer val) {
		T p = this.factory.find(val);
		if( p != null ){
			return finder.getCanonicalName(p);
		}
		return "";
	}
	/**
	 * @return the create
	 */
	public boolean isCreate() {
		return create;
	}
	/**
	 * @param create the create to set
	 */
	public void setCreate(boolean create) {
		this.create = create;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.AbstractInput#convert(java.lang.Object)
	 */
	@Override
	public Integer convert(Object v) throws TypeException {
		if( v == null) {
			return null;
		}
		if( v instanceof DataObject){
			if( factory.isMine(v)){
				return Integer.valueOf(((T)v).getID());
			}else{
				throw new TypeException("DataObject "+v.getClass().getCanonicalName()+" passed to "+getClass().getCanonicalName());
			}
		}
		if( v instanceof IndexedReference ){
			if( factory.isMyReference((IndexedReference) v)){
				return Integer.valueOf(((IndexedReference)v).getID());
			}else{
				throw new TypeException("IndexedReference "+v.toString()+" passed to "+getClass().getCanonicalName());
			}
		}
		if( v instanceof Number) {
			int intValue = ((Number)v).intValue();
			if( intValue <= 0 ) {
				// This is for legacy references where "null" references are stored
				// as 0 or -1
				return null;
			}
			return Integer.valueOf(intValue);
		}
		if(v instanceof String  ) {
			String name = (String)v;
			if( ! name.trim().isEmpty()) {
				T item = finder.findFromString(name);
				if( item != null) {
					return item.getID();
				}
			}
		}
		return super.convert(v);
	}
	public String getMatchError() {
		return match_error;
	}
	public void setMatchError(String match_error) {
		this.match_error = match_error;
	}
	@Override
	public boolean useAutoComplete() {
		return use_autocomplete;
	}
	
	public void setUseAutoComplete(boolean val) {
		use_autocomplete = val;
	}
	
	
	
}