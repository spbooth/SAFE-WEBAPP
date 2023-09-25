package uk.ac.ed.epcc.webapp.model.data.forms.inputs;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
import uk.ac.ed.epcc.webapp.forms.inputs.*;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter;
import uk.ac.ed.epcc.webapp.model.NameFinder;
import uk.ac.ed.epcc.webapp.model.ParseFactory;
import uk.ac.ed.epcc.webapp.model.data.AbstractDataObjectInput;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedProducer;

/** An {@link DataObjectItemInput} for {@link ParseFactory}/{@link NameFinder} factories.
 * 
 * This is an {@link AutoCompleteListInput} that can be presented as either a pull-down list or
 * an auto-complete text box. Optionally if created with a {@link NameFinder} it can be used to create new bootstrap
 * entries. Though this requires tet-box presentation. 
 * 
 * In text-box mode it is also possible to turn off the auto-completion list for selections where the
 * suggestion list is too long.
 * 
 * @author spb
 *
 */
public class NameFinderInput<T extends DataObject,F extends DataObjectFactory<T>> extends AbstractDataObjectInput<T> implements AutoCompleteListInput<Integer, T>, PreSelectInput<Integer, T>, FormatHintInput{
	protected final ParseFactory<T> finder; // in most cases this will aslo be the factory but not always
	
	/** Possible operating modes.
	 * 
	 * This is the requested operating mode. 
	 */
	public static enum Options{
		CREATE(true,false,true),
		CREATE_NO_SUGGESTIONS(true,false,false),
		LIST(false,true,true),
		AUTO_COMPLETE(false,false,true),
		TEXT_ONLY(false,false,false);
		private Options(boolean create, boolean list, boolean suggestions) {
			this.create = create;
			this.list = list;
			this.suggestions = suggestions;
		}
		final boolean create; // Create new objects, requires text presentations and a NameFinder
		final boolean list;   // Force list presentation
		final boolean suggestions; // generate suggestions in text mode
	
	}
	
	public NameFinderInput(F factory,ParseFactory<T> finder,BaseFilter<T> restrict,BaseFilter<T> autocomplete) {
		this(factory,finder,Options.AUTO_COMPLETE,restrict,autocomplete);
	}
	public NameFinderInput(F factory,ParseFactory<T> finder,boolean create,BaseFilter<T> restrict,BaseFilter<T> autocomplete) {
		this(factory,finder,create ? Options.CREATE: Options.AUTO_COMPLETE,restrict,autocomplete);
	}
	/**
	 * 
	 * @param factory
	 * @param finder
	 * @param options
	 * @param restrict
	 * @param autocomplete
	 */
	public NameFinderInput(F factory, ParseFactory<T> finder,Options options, BaseFilter<T> restrict,BaseFilter<T> autocomplete) {

		super(factory,autocomplete,restrict);
		this.finder = finder;
		this.options=options;
		


	}
	private Options options;
	
	public void setOptions(Options opt) {
		options = opt;
	}
	
	
	public T parseItem(String v) throws ParseException {
		if( v == null || v.trim().isEmpty()) {
			return null;
		}
		try{
			boolean created = false;
			T target=finder.findFromString(v);
			if( target == null && canCreate() ){
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
		T p = getItembyValue(val);
		if( p != null ){
			return getValue(p);
		}
		return Integer.toString(val);
	}
	/**
	 * @return the create
	 */
	public boolean canCreate() {
		return options.create && (finder instanceof NameFinder);
	}
	
	
	
	
	
	
	@Override
	public boolean canSubmit() {
		if( canCreate() ) {
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
		if( canCreate() ) {
			return null;
		}
		try {
			if( getRestrictionCount() == 1L) {
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
	
	private int boxwid=32;
	@Override
	public int getBoxWidth() {
		return boxwid;
	}
	@Override
	public void setBoxWidth(int l) {
		boxwid=l;
	}
	@Override
	public boolean getSingle() {
		return true;
	}
	@Override
	public boolean useListPresentation() {
		if( options.list || finder == null) {
			return true;
		}
		if( ! options.suggestions) {
			return false;  // suggtions explicitly not requested
		}
		if( options.create) {
			return false; // cannot create from list
		}
		try {
			int threshold = getConfigParam("list_threshold", 50);
			if( threshold > 0 ) {
				// If the possible suggestions are low enough and
				// the suggestions contain all the allowed choices then we can use a list
				long allowed_count = getRestrictionCount();
				if( allowed_count < threshold) {
					if( allowed_count ==  getCount()) {
						return true;
					}

				}
			}
		} catch (DataException e) {
			getLogger().error("Error in presentation choice", e);
		}
		return false;
	}
	
	
	/** Get a configuration parameter.
	 * Check FactoryTag.name first then NameFinderInput.name
	 * 
	 * @param name
	 * @param def
	 * @return
	 */
	private int getConfigParam(String name,int def) {
		AppContext conn = getContext();
		return conn.getIntegerParameter(getFactory().getConfigTag()+"."+name,
				conn.getIntegerParameter("NameFinderInput."+name, def)
				);
	}

	@Override
	public boolean useAutoComplete() {
		if( ! options.suggestions) {
			return false;  // explicitly disables
		}
		int max = getConfigParam("max_datalist", 200);
		if( max > 0 && getCount() > max) {
			return false; // too many values to add a datalist
		}
		
		return true;
	}
	
	
}