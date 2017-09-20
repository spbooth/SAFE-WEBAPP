package uk.ac.ed.epcc.webapp.model;

import java.util.Map;
import java.util.Set;

import uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.GenericBinaryFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.MatchCondition;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLAndFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLOrFilter;
import uk.ac.ed.epcc.webapp.jdbc.table.ReferenceFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.NamedFilterProvider;
import uk.ac.ed.epcc.webapp.model.data.SelectModifier;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.filter.NullFieldFilter;
import uk.ac.ed.epcc.webapp.model.data.filter.SQLValueFilter;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** A {@link ServiceFilterComposite} that uses a reference to a classifier table
 * to indicate the service.
 * 
 * This ony supports filtering where each target is in a single service or all services.
 * 
 * @author spb
 *
 * @param <BDO>
 */
public class ReferenceServiceFilterComposite<BDO extends DataObject> extends ServiceFilterComposite<BDO> implements  NamedFilterProvider<BDO>, SelectModifier<BDO>{

	private static final String SERVICE_CLASSIFIER="Services";
	
	
	
	private static final String SERVICE_ID_FIELD = "ServicesID";

	public ReferenceServiceFilterComposite(DataObjectFactory<BDO> fac) {
		super(fac);
	}

	

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.Composite#modifyDefaultTableSpecification(uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification, java.lang.String)
	 */
	@Override
	public TableSpecification modifyDefaultTableSpecification(TableSpecification spec, String table) {
		spec.setOptionalField(SERVICE_ID_FIELD, new ReferenceFieldType(true, SERVICE_CLASSIFIER));
		
		return spec;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.Composite#addSuppress(java.util.Set)
	 */
	@Override
	public Set<String> addSuppress(Set<String> suppress) {
		// Only developers can set this
		if( ! getContext().getService(SessionService.class).hasRole(SET_SERVICE_ROLE)){
			suppress.add(SERVICE_ID_FIELD);
		}
		return suppress;
	}

	/** get a filter for records that match the current service
	 * 
	 * @return
	 */
	public SQLFilter<BDO> getCurrentServiceFilter(){
		Class<? super BDO> target = getFactory().getTarget();
		if(getRepository().hasField(SERVICE_ID_FIELD)){
			int[] ids = getCurrentIDs();
			SQLFilter<BDO>[] filters = new SQLFilter[ids.length+1];
			int i;
			for (i = 0; i < ids.length; i++) {
				filters[i] = new SQLValueFilter<BDO>(target, getRepository(), SERVICE_ID_FIELD, ids[i]);
			}
			filters[i] = new NullFieldFilter<BDO>(target, getRepository(), SERVICE_ID_FIELD, true);
			return new SQLOrFilter<>(target, filters);
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
		if(getRepository().hasField(SERVICE_ID_FIELD)){
			int[] ids = getCurrentIDs();
			SQLFilter<BDO>[] filters = new SQLFilter[ids.length];
			int i;
			for (i = 0; i < ids.length; i++) {
				filters[i] = new SQLValueFilter<BDO>(target, getRepository(), SERVICE_ID_FIELD, MatchCondition.NE, ids[i]);
			}
			return new SQLAndFilter<>(target, filters);
		}else{
			return new GenericBinaryFilter<>(target, false);
		}

	}
	
	public boolean isCurrentService(BDO obj){
		int id = getRecord(obj).getIntProperty(SERVICE_ID_FIELD, 0);
		if (id == 0) return true;
		int[] ids = getCurrentIDs();
		for (int currid: ids) {
			if (id == currid) return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.Composite#addOptional(java.util.Set)
	 */
	@Override
	public Set<String> addOptional(Set<String> optional) {
		if( optional != null ){
			optional.add(SERVICE_ID_FIELD);
		}
		return optional;
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
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.NamedFilterProvider#addFilterNames(java.util.Set)
	 */
	@Override
	public void addFilterNames(Set<String> names) {
		names.add(THIS_SERVICE_FILTER_NAME);
		names.add(OTHER_SERVICE_FILTER_NAME);
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.Composite#addSelectors(java.util.Map)
	 */
	@Override
	public Map<String, Object> addSelectors(Map<String, Object> selectors) {
		ClassificationFactory<Classification> fac = getServicesFactory();
		selectors.put(SERVICE_ID_FIELD, fac);
		return selectors;
	}

	/**
	 * @return
	 */
	protected ClassificationFactory getServicesFactory() {
		return getContext().makeObject(ClassificationFactory.class, SERVICE_CLASSIFIER);
	}
	
	private int[] ids=null;
	
	private int[] getCurrentIDs(){
		if ((ids == null) && (!getRepository().hasField(SERVICE_ID_FIELD))) {
			ids = new int[1];
			ids[0] = 0;
		}
		
		if (ids != null) return ids;
		
		try {
			String namelist = getContext().getInitParameter(SERVICE_LIST_PARAM);
			if (namelist == null) {
				namelist = getContext().getInitParameter(SERVICE_NAME_PARAM);
			}
			String[] names = namelist.split(",");
			ids = new int[names.length];
			for (int i = 0; i < names.length; i++) {
				String name = names[i];
				Classification current = getServicesFactory().makeFromString(name);
				if (current != null) {
					current.commit();
					ids[i] = current.getID();
				}
			}
			
		} catch (DataFault e) {
			getLogger().error("Error looking up serviceID", e);
		}
		return ids;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.SelectModifier#getSelectFilter()
	 */
	@Override
	public BaseFilter<BDO> getSelectFilter() {
		return getCurrentServiceFilter();
	}

	
}
