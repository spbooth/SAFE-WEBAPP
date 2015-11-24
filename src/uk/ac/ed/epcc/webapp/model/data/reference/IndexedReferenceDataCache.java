// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data.reference;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Indexed;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.model.data.DataCache;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
/** A DataCache keyed by IndexedReference objects.
 * This needs no additional logic to create the target objects as the IndexedRefenece can do this 
 * directly. However the use of a cache can reduce the number of database lookups.
 * 
 * @author spb
 *
 * @param <I>
 */
@uk.ac.ed.epcc.webapp.Version("$Id: IndexedReferenceDataCache.java,v 1.3 2014/09/15 14:30:32 spb Exp $")

public class IndexedReferenceDataCache<I extends Indexed> extends DataCache<IndexedReference<? extends I>, I> {
    private AppContext c;
    public IndexedReferenceDataCache(AppContext c){
    	this.c=c;
    }
	@Override
	protected I find(IndexedReference<? extends I> key) throws DataException {
		try {
			return key.getIndexed(c);
		} catch (Exception e) {
			throw new DataFault("Error making target from IndexedReference",e);
		}
	}

}