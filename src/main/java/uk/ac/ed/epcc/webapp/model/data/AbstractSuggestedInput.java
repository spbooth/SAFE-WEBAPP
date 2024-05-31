package uk.ac.ed.epcc.webapp.model.data;

import java.util.Iterator;

import uk.ac.ed.epcc.webapp.forms.inputs.SuggestedItemInput;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.FilterConverter;
import uk.ac.ed.epcc.webapp.jdbc.filter.NoSQLFilterException;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory.DataObjectFieldValidator;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory.FilterIterator;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.iterator.EmptyIterator;
import uk.ac.ed.epcc.webapp.validation.FieldValidator;

public abstract class AbstractSuggestedInput<BDO extends DataObject> extends DataObjectIntegerInput<BDO> implements SuggestedItemInput<Integer, BDO>{
	private BaseFilter<BDO> select_fil=null; // narrow selection without restircting parse
	
	public AbstractSuggestedInput(DataObjectFactory factory,BaseFilter<BDO> view_fil,BaseFilter<BDO> restrict_fil) {
		super(factory);
		if( view_fil != null) {
			BaseFilter<BDO> fil;
			try{ 
				fil = FilterConverter.convert(view_fil);
			}catch(NoSQLFilterException e){
				fil = view_fil;
			}
			select_fil = fil;
		}
		if( restrict_fil != null) {
			try {
				addValidator(getValidator(FilterConverter.convert(restrict_fil)));
			}catch(NoSQLFilterException e) {
				addValidator(getValidator(restrict_fil));
			}
		}
		
					
	}
	/** convert a {@link BaseFilter} on item types into a {@link FieldValidator}
	 * 
	 * 
	 * 
	 * @param fil
	 * @return
	 */
	protected FieldValidator getValidator(BaseFilter<BDO> fil) {
		// The default is to validate the object ids.
		return getFactory().new DataObjectFieldValidator(fil);
	}
	
	@Override
	public int getCount() {
		try {
			return (int) getFactory().getCount(getFil());
		} catch (DataException e) {
			getLogger().error("Error counting items",e);
			return 0;
		}
		
	}
	@Override
	public Iterator<BDO> getItems() {
		try {
			return getFactory().new FilterIterator(getFil());
		} catch (DataFault e) {
			getLogger().error("Error making select Iterator",e);
			return new EmptyIterator<>();
		}
	}
	/** Get the suggestion filter narrowed by any validators
	 * 
	 * @return
	 */
	private BaseFilter<BDO> getFil() {
		uk.ac.ed.epcc.webapp.jdbc.filter.AndFilter and = getFactory().getAndFilter(select_fil);
		for(FieldValidator<Integer> v : getValidators()) {
			if(v instanceof DataObjectFactory.DataObjectFieldValidator) {
				and.addFilter(((DataObjectFactory.DataObjectFieldValidator)v).getFilter());
			}
		}
		return and;
	}
	protected BaseFilter<BDO> getRestrictionFilter() {
		uk.ac.ed.epcc.webapp.jdbc.filter.AndFilter and = getFactory().getAndFilter();
		for(FieldValidator<Integer> v : getValidators()) {
			if(v instanceof DataObjectFactory.DataObjectFieldValidator) {
				and.addFilter(((DataObjectFactory.DataObjectFieldValidator)v).getFilter());
			}
		}
		return and;
	}
	protected long getRestrictionCount() throws DataException {
		return getFactory().getCount(getRestrictionFilter());
	}
	
}