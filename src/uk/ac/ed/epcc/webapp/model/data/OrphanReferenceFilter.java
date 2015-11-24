// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.model.data;

import uk.ac.ed.epcc.webapp.model.data.filter.OrphanFilter;

/**
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.1 $")
public class OrphanReferenceFilter<T extends DataObject,BDO extends DataObject> extends OrphanFilter<T, BDO> {

	/**
	 * 
	 */
	public OrphanReferenceFilter(DataObjectFactory<BDO> fac, String field,DataObjectFactory<T> remote_fac) {
		super(fac.getTarget(), field,fac.res,remote_fac.res);
	}

}
