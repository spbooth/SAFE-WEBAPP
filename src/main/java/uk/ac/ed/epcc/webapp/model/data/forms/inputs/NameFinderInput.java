package uk.ac.ed.epcc.webapp.model.data.forms.inputs;

import java.util.LinkedHashSet;
import java.util.Set;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
import uk.ac.ed.epcc.webapp.forms.inputs.*;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.NameFinder;
import uk.ac.ed.epcc.webapp.model.ParseFactory;
import uk.ac.ed.epcc.webapp.model.data.AbstractDataObjectInput;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedProducer;
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
public class NameFinderInput<T extends DataObject,F extends DataObjectFactory<T>> extends AbstractDataObjectInput<T> implements AutoCompleteListInput<Integer, T>, PreSelectInput<Integer, T>, FormatHintInput{
	protected final ParseFactory<T> finder; // in most cases this will aslo be the factory but not always
	
	
	/** create input
	 * 
	 * @param create   make entry if not found
	 * @param restrict  restrict with filter
	 * @param autocomplete  suggestions/restrict filter
	 * @param factory {@link DataObjectFactory} and {@link NameFinder}
	 */
	public NameFinderInput(F factory, ParseFactory<T> finder,boolean create, BaseFilter<T> restrict,BaseFilter<T> autocomplete) {
		this(factory,finder,create,true,restrict,autocomplete);
	}
	/** create input
	 * 
	 * @param create   make entry if not found
	 * @param use_autocomplete   use autocomplete input or a simple text input
	 * @param restrict  restrict filter
	 * @param autocomplete  suggestions filter
	 * @param factory {@link DataObjectFactory} and {@link NameFinder}
	 */
	public NameFinderInput(F factory, ParseFactory<T> finder,boolean create, boolean use_autocomplete, BaseFilter<T> restrict,BaseFilter<T> autocomplete) {

		super(factory,autocomplete,restrict);
		this.finder = finder;
		this.create = create && (finder instanceof NameFinder);
		this.use_autocomplete = use_autocomplete;
		
		setSingle(true);
		addValidator(factory.new DataObjectFieldValidator(restrict));
	}
	private boolean create;
	private boolean use_autocomplete;  // allow autocomplete to be turned off 
	
	
	public T parseItem(String v) throws ParseException {
		if( v == null || v.trim().isEmpty()) {
			return null;
		}
		try{
			boolean created = false;
			T target=finder.findFromString(v);
			if( target == null && create ){
				NameFinder<T> nf = (NameFinder<T>) finder;
				nf.validateNameFormat(v);
				target=nf.makeFromString(v);
				created=true;
			}
			if(target == null) {
				// Fallback to trying an integer parse
				// if we are also an IndexedProducer
				if( finder instanceof IndexedProducer) {
					try {
						int id = Integer.valueOf(v);
						target = ((IndexedProducer<T>)finder).find(id);
					}catch(Exception e) {

					}
				}
				if( target == null ) {
					throw new ParseException("["+v+"] Not found");
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
		kkkk
		if( v == null || v.trim().isEmpty()) {
			return null;
		}
		T item = parseItem(v);
		if( item == null) {
			try {
				return Integer.parseInt(v);
			}catch(NumberFormatException nf) {
				return null;
			}
		}
		return getValueByItem(item);
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
		
		MUST mutate ???
		T p = getItembyValue(val);
		if( p != null ){
			return getValue(p);
		}
		return Integer.toString(val);
	}
	/**
	 * @return the create
	 */
	public boolean isCreate() {
		return create;
	}
	/**
	 * @param create enable/disable object creation
	 */
	public void setCreate(boolean create) {
		if( ! (finder instanceof NameFinder)) {
			return;
		}
		this.create = create;
	}
	
	
	@Override
	public boolean useAutoComplete() {
		return use_autocomplete;
	}
	
	public void setUseAutoComplete(boolean val) {
		use_autocomplete = val;
	}
	@Override
	public boolean canSubmit() {
		if( create ) {
			return true;
		}
		try {
			return getFactory().exists(getRestrictionFilter());
		} catch (DataException e) {
			getLogger().error("Error checking submit", e);
			return false;
		}
	}
	@Override
	public T forcedItem() {
		if( create ) {
			return null;
		}
		try {
			if( getFactory().getCount(getRestrictionFilter()) == 1L) {
				return getFactory().find(getRestrictionFilter());
			}
		} catch (DataException e) {
			getLogger().error("Error checking for forced", e);
		}
		return null;
	}
	
	
	private String format_hint=null;
	public void setFormatHint(String hint) {
		format_hint=hint;
	}
	@Override
	public String getFormatHint() {
		return format_hint;
	}
	
	
}