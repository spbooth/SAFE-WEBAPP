// Copyright - The University of Edinburgh 2014
package uk.ac.ed.epcc.webapp.model.data;

import uk.ac.ed.epcc.webapp.jdbc.filter.MatchCondition;
import uk.ac.ed.epcc.webapp.model.data.filter.SQLValueFilter;

/** A version of {@link SQLValueFilter} specifically for {@link DataObjectFactory}s
 * 
 * 
 * It is a good idea to subclass
 * again so as to improve type safety and hide the field name.
 * <p>
 * 
 * @author spb
 * @param <BDO> type of factory
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: ValueFilter.java,v 1.3 2014/09/15 14:30:29 spb Exp $")
public class ValueFilter<BDO extends DataObject> extends SQLValueFilter<BDO> {

	/**
	 * 
	 * @param fac
	 * @param field
	 * @param cond
	 * @param peer
	 */
	public ValueFilter(DataObjectFactory<BDO> fac, String field,
			MatchCondition cond, Object peer) {
		super(fac.getTarget(), fac.res, field, cond, peer);
		
	}

	/**
	 * @param target
	 * @param res
	 * @param field
	 * @param peer
	 * @param negate
	 */
	public ValueFilter(DataObjectFactory<BDO> fac, String field,
			Object peer, boolean negate) {
		super(fac.getTarget(), fac.res, field, peer, negate);
	}

	/**
	 * @param target
	 * @param res
	 * @param field
	 * @param peer
	 */
	public ValueFilter(DataObjectFactory<BDO> fac, String field,
			Object peer) {
		super(fac.getTarget(),fac.res,field, peer);
	}

}
