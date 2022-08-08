package uk.ac.ed.epcc.webapp.model;

import java.util.Map;
import java.util.Set;

import uk.ac.ed.epcc.webapp.jdbc.filter.*;
import uk.ac.ed.epcc.webapp.jdbc.table.ReferenceFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.data.*;
import uk.ac.ed.epcc.webapp.model.data.filter.NullFieldFilter;
import uk.ac.ed.epcc.webapp.model.data.filter.SQLValueFilter;
import uk.ac.ed.epcc.webapp.model.data.forms.Selector;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** A {@link ServiceFilterComposite} that uses a reference to a classifier table
 * to indicate the service. This is used to support different views of the same database.
 * <p>
 * An object is considered to be part of the view if it has a null reference to a service or if it references one of the
 * services in the parameter <b>service.list</b> (comma separated service names from the classifier <b>Services</b>).
 * <p>
 * The default selector will also be narrowed to the current view unless the parameter
 * <b>ServiceFilterComposite.<i>config-tag</i>.auto_narrow</b> is set to false. In which case the owning factory should have
 * an explicit relationship for setting the selector which can reference the named filters. Narrowing the selectors to the current view is a sensible
 * default though we may want to put in exceptions for some users.
 * 
 * @author spb
 *
 * @param <BDO>
 */
public class ReferenceServiceFilterComposite<BDO extends DataObject> extends ServiceFilterComposite<BDO> implements  NamedFilterProvider<BDO>, SelectModifier<BDO>{

	private static final String SERVICE_CLASSIFIER="Services";
	
	
	protected static final String SERVICE_LIST_PARAM = "service.list";
	private static final String SERVICE_ID_FIELD = "ServicesID";

	private boolean auto_narrow=true;
	public ReferenceServiceFilterComposite(DataObjectFactory<BDO> fac) {
		super(fac);
		auto_narrow = fac.getContext().getBooleanParameter("ServiceFilterComposite."+fac.getConfigTag()+".auto_narrow", true);
	}

	

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.Composite#modifyDefaultTableSpecification(uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification, java.lang.String)
	 */
	@Override
	public TableSpecification modifyDefaultTableSpecification(TableSpecification spec, String table) {
		spec.setOptionalField(SERVICE_ID_FIELD, new ReferenceFieldType(SERVICE_CLASSIFIER));
		
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
	 * @return {@link SQLFilter}
	 */
	@Override
	public SQLFilter<BDO> getCurrentServiceFilter(){
		Class<BDO> target = getFactory().getTarget();
		int[] currentIDs = getCurrentIDs();
		if(currentIDs != null && currentIDs.length > 0){
			// Match any id in current view list or null.
			SQLOrFilter<BDO> fil = new SQLOrFilter<>(target);
			for(int id : currentIDs) {
				fil.addFilter(new SQLValueFilter<>(target, getRepository(), SERVICE_ID_FIELD, id) );
			}
			fil.addFilter(new NullFieldFilter<>(target, getRepository(), SERVICE_ID_FIELD, true));
			return fil;
		}else{
			return new GenericBinaryFilter<>(target, true);
		}
	}
	
	/** get a filter for records that are not part of the current service
	 * 
	 * @return {@link SQLFilter}
	 */
	@Override
	public SQLFilter<BDO> getOtherServiceFilter(){
		Class<BDO> target = getFactory().getTarget();
		int[] currentIDs = getCurrentIDs();
		if(currentIDs != null && currentIDs.length > 0){
			SQLAndFilter<BDO> fil = new SQLAndFilter<>(target);
			
			for( int id : currentIDs) {
				fil.addFilter(new SQLValueFilter<>(target, getRepository(), SERVICE_ID_FIELD, MatchCondition.NE, id));
			}
			return fil;
		}else{
			return new GenericBinaryFilter<>(target, false);
		}

	}
	
	@Override
	public boolean isCurrentService(BDO obj){
		int id = getRecord(obj).getIntProperty(SERVICE_ID_FIELD, 0);
		if (id == 0) return true;
		int[] ids = getCurrentIDs();
		if( ids == null || ids.length ==0 ) {
			return true;
		}
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
	public Map<String, Selector> addSelectors(Map<String, Selector> selectors) {
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



	
	/** get an array of the services in the current view
	 * a null value or a zero length list means show all services.
	 * @return
	 */
	private int[] getCurrentIDs(){
		if ((ids == null) && (!getRepository().hasField(SERVICE_ID_FIELD))) {
			ids = new int[0];
		}
		
		if (ids != null) return ids;
		
		try {
			String namelist = getContext().getInitParameter(ReferenceServiceFilterComposite.SERVICE_LIST_PARAM);
			if (namelist == null || namelist.isEmpty()) {
				namelist = getContext().getInitParameter(SERVICE_NAME_PARAM);
			}
			if( namelist == null || namelist.isEmpty()){
				ids = new int[0];
				return ids;
			}
			String[] names = namelist.trim().split(",");
			ids = new int[names.length];
			for (int i = 0; i < names.length; i++) {
				String name = names[i].trim();
				if( ! name.isEmpty() ){
					Classification current = getServicesFactory().makeFromString(name);
					if (current != null) {
						current.commit();
						ids[i] = current.getID();
					}
				}
			}
			
		} catch (Exception e) {
			getLogger().error("Error looking up serviceID", e);
		}
		return ids;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.SelectModifier#getSelectFilter()
	 */
	@Override
	public BaseFilter<BDO> getSelectFilter() {
		if( auto_narrow) {
			return getCurrentServiceFilter();
		}
		return null;
	}

	
}
