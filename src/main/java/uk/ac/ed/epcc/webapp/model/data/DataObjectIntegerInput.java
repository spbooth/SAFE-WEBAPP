package uk.ac.ed.epcc.webapp.model.data;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.inputs.AbstractInput;
import uk.ac.ed.epcc.webapp.forms.inputs.TypeException;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.model.data.forms.inputs.DataObjectItemInput;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedReference;


public abstract class DataObjectIntegerInput<BDO extends DataObject> extends AbstractInput<Integer> implements DataObjectItemInput<BDO> {

    /**
	 * 
	 */
	private final DataObjectFactory<BDO> factory;


	public DataObjectIntegerInput(DataObjectFactory<BDO> dataObjectFactory) {
		super();
		factory = dataObjectFactory;
	}

	protected  final DataObjectFactory<BDO> getFactory(){
		return factory;
	}
	
	protected final  Logger getLogger() {
		return factory.getLogger();
	}
	protected final AppContext getContext() {
		return factory.getContext();
	}
	@Override
	public BDO getItembyValue(Integer num) {
        if (num == null || num.intValue() <= 0) {
            // must be optional
            return null;
        }
        try {
            return factory.find(num.intValue());
        } catch (DataException e) {
            return null;
        }
    }
	@Override
	public Integer getValueByItem(BDO item) {
		if( item == null) {
			return null;
		}
		return item.getID();
	}
    

	@SuppressWarnings("unchecked")
	@Override
	public final Integer convert(Object v) throws TypeException {
		if( v == null ){
			return null;
		}
		if( v instanceof DataObject){
			if( factory.isMine(v)){
				return getValueByItem((BDO)v);
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
		
		throw new TypeException(v.getClass());
	}

	
	
}