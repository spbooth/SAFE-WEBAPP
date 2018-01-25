package uk.ac.ed.epcc.webapp.model.data.forms.inputs;

import java.util.LinkedHashSet;
import java.util.Set;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;
import uk.ac.ed.epcc.webapp.forms.inputs.AutoComplete;
import uk.ac.ed.epcc.webapp.forms.inputs.ParseAbstractInput;
import uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.NameFinder;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;

/** An {@link DataObjectItemInput} for {@link NameFinder} factories.
 * 
 * This is a text {@link AutoComplete} input using the permitted names
 * optionally creating bootstrap entries 
 * 
 * @author spb
 *
 */
public class NameFinderInput<T extends DataObject,F extends DataObjectFactory<T>&NameFinder<T>> extends ParseAbstractInput<Integer> implements AutoComplete<T, Integer>, DataObjectItemInput<T>{
	/**
	 * 
	 */
	protected final F factory;
	/** create input
	 * 
	 * @param create   make entry if not found
	 * @param restrict  restrict with filter
	 * @param autocomplete  suggestions/restrict filter
	 * @param factory {@link DataObjectFactory} and {@link NameFinder}
	 */
	public NameFinderInput(F factory, boolean create, boolean restrict,BaseFilter<T> autocomplete) {
		super();
		this.factory = factory;
		this.create = create;
		this.restrict=restrict;
		this.autocomplete = autocomplete;
	}
	private boolean create;
	private final boolean restrict;
	private final BaseFilter<T> autocomplete;
	@Override
	public void parse(String v) throws ParseException {
		if( v == null || v.trim().isEmpty()) {
			setItem(null);
			return;
		}
		factory.validateNameFormat(v);
		try{
			T target=null;
			if( create){
				target=factory.makeFromString(v);
			}else{
				target=factory.findFromString(v);
				
			}
			if(target == null) {
				throw new ParseException("Not found");
			}
			setItem(target);
		}catch(ParseException p) {
			throw p;
		}catch(Exception e){
			throw new ParseException(e);
		}
	}
	@Override
	public T getItem() {
		return factory.find(getValue());
	}
	@Override
	public void setItem(T item) {
		if( item == null){
			setValue(null);
		}else{
			setValue(item.getID());
		}
	}
	@Override
	public Set<T> getSuggestions() {
		LinkedHashSet<T> result = new LinkedHashSet<T>();
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
		return factory.getCanonicalName(item);
	}
	@Override
	public String getSuggestionText(T item) {
		return getValue(item)+": "+item.getIdentifier();
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.AbstractInput#validate()
	 */
	@Override
	public void validate() throws FieldException {
		super.validate();
		if( getValue() == null) {
			// optional input
			return;
		}
		if( restrict){
			if( ! this.factory.matches(autocomplete, getItem())){
				throw new ValidateException("Input does not match required filter");
			}
		}
	}
	@Override
	public T getDataObject() {
		return getItem();
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.AbstractInput#getString(java.lang.Object)
	 */
	@Override
	public String getString(Integer val) {
		T p = this.factory.find(val);
		if( p != null ){
			return factory.getCanonicalName(p);
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
	
}