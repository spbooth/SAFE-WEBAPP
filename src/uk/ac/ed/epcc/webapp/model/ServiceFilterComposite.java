package uk.ac.ed.epcc.webapp.model;

import java.util.Map;
import java.util.Set;

import uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.GenericBinaryFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.MatchCondition;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLOrFilter;
import uk.ac.ed.epcc.webapp.jdbc.table.StringFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.data.Composite;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.NamedFilterProvider;
import uk.ac.ed.epcc.webapp.model.data.filter.NullFieldFilter;
import uk.ac.ed.epcc.webapp.model.data.filter.SQLValueFilter;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** A composite to allow records to be filtered by service.name
 * This is to provide different views of the same database.
 * 
 * @author spb
 *
 * @param <BDO>
 */
public class ServiceFilterComposite<BDO extends DataObject> extends Composite<BDO, ServiceFilterComposite> implements NamedFilterProvider<BDO>{

	
	/**
	 * 
	 */
	private static final String OTHER_SERVICE_FILTER_NAME = "OtherService";
	/**
	 * 
	 */
	private static final String THIS_SERVICE_FILTER_NAME = "ThisService";
	/**
	 * 
	 */
	private static final String SET_SERVICE_ROLE = "SetService";
	private static final String SERVICE_NAME_PARAM = "service.name";
	private static final String SERVICE_NAME_FIELD = "ServiceName";

	public ServiceFilterComposite(DataObjectFactory<BDO> fac) {
		super(fac);
	}

	@Override
	protected Class<? super ServiceFilterComposite> getType() {
		return ServiceFilterComposite.class;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.Composite#modifyDefaultTableSpecification(uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification, java.lang.String)
	 */
	@Override
	public TableSpecification modifyDefaultTableSpecification(TableSpecification spec, String table) {
		spec.setOptionalField(SERVICE_NAME_FIELD, new StringFieldType(true, null, 16));
		
		return spec;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.Composite#addSuppress(java.util.Set)
	 */
	@Override
	public Set<String> addSuppress(Set<String> suppress) {
		// Only develoeprs can set this
		if( ! getContext().getService(SessionService.class).hasRole(SET_SERVICE_ROLE)){
			suppress.add(SERVICE_NAME_FIELD);
		}
		return suppress;
	}

	/** get a filter for records that match the current service
	 * 
	 * @return
	 */
	public SQLFilter<BDO> getCurrentServiceFilter(){
		Class<? super BDO> target = getFactory().getTarget();
		if(getRepository().hasField(SERVICE_NAME_FIELD)){

			return new SQLOrFilter<>(target, 
					new SQLValueFilter<BDO>(target, getRepository(), SERVICE_NAME_FIELD, getContext().getInitParameter(SERVICE_NAME_PARAM)),
					new NullFieldFilter<BDO>(target,getRepository(),SERVICE_NAME_FIELD,true)
					);
		}else{
			return new GenericBinaryFilter<BDO>(target, true);
		}
	}
	
	/** get a filter for records that are not part of the current service
	 * 
	 * @return
	 */
	public SQLFilter<BDO> getOtherServiceFilter(){
		Class<? super BDO> target = getFactory().getTarget();
		if(getRepository().hasField(SERVICE_NAME_FIELD)){

			return new SQLValueFilter<BDO>(target, getRepository(), SERVICE_NAME_FIELD, MatchCondition.NE, getContext().getInitParameter(SERVICE_NAME_PARAM));
		}else{
			return new GenericBinaryFilter<>(target, false);
		}

	}
	
	public boolean isCurrentService(BDO obj){
		String name = getRecord(obj).getStringProperty(SERVICE_NAME_FIELD,null);
		return name== null || name.equals(getContext().getInitParameter(SERVICE_NAME_PARAM));
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.Composite#addOptional(java.util.Set)
	 */
	@Override
	public Set<String> addOptional(Set<String> optional) {
		if( optional != null ){
			optional.add(SERVICE_NAME_FIELD);
		}
		return optional;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.Composite#addDefaults(java.util.Map)
	 */
	@Override
	public Map<String, Object> addDefaults(Map<String, Object> defaults) {
		defaults.put(SERVICE_NAME_FIELD, getContext().getInitParameter(SERVICE_NAME_PARAM));
		return defaults;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.NamedFilterProvider#getNamedFilter(java.lang.String)
	 */
	@Override
	public BaseFilter<BDO> getNamedFilter(String name) {
		if( name.equals(THIS_SERVICE_FILTER_NAME)){
			return getCurrentServiceFilter();
		}else if( name.equals(OTHER_SERVICE_FILTER_NAME)){
			return getOtherServiceFilter();
		}
		return null;
	}
}
