package uk.ac.ed.epcc.webapp.model;

import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.model.data.Composite;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.NamedFilterProvider;
import uk.ac.ed.epcc.webapp.model.data.SelectModifier;

/** An abstract {@link Composite} to allow records to be filtered by service.name
 * This is to provide different views of the same database.
 * Composites should register under this type
 * 
 * @author spb
 *
 * @param <BDO>
 */
public abstract class ServiceFilterComposite<BDO extends DataObject> extends  Composite<BDO, ServiceFilterComposite> implements NamedFilterProvider<BDO>, SelectModifier<BDO>{

	
	public ServiceFilterComposite(DataObjectFactory<BDO> fac) {
		super(fac);
	}
	
	@Override
	protected final Class<? super ServiceFilterComposite> getType() {
		return ServiceFilterComposite.class;
	}

	/** Name to return {@link #getOtherServiceFilter()} from {@link NamedFilterProvider#getNamedFilter(String)}
	 * 
	 */
    protected static final String OTHER_SERVICE_FILTER_NAME = "OtherService";
	/** Name to return {@link #getCurrentServiceFilter()} from {@link NamedFilterProvider#getNamedFilter(String)}
	 * 
	 */
	protected static final String THIS_SERVICE_FILTER_NAME = "ThisService";
	
	protected static final String SERVICE_NAME_PARAM = "service.name";

	
	/** The access role needed to edit service mappings.
	 * 
	 */
	protected static final String SET_SERVICE_ROLE = "SetService";
	
	


	/** get a filter for records that match the current service
	 * 
	 * @return {@link SQLFilter}
	 */
	public abstract SQLFilter<BDO> getCurrentServiceFilter();
	
	/** get a filter for records that are not part of the current service
	 * 
	 * @return {@link SQLFilter}
	 */
	public abstract SQLFilter<BDO> getOtherServiceFilter();
	
	public abstract boolean isCurrentService(BDO obj);
	
}
