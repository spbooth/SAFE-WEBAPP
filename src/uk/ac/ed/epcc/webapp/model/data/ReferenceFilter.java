// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data;

import uk.ac.ed.epcc.webapp.Indexed;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLFilter;
import uk.ac.ed.epcc.webapp.model.data.filter.SQLValueFilter;

/** Generic  SQL filter for selecting records using a
 * peer Object that implements Indexed referenced from a
 * integer field.
 * <p>
 * As DataObject implements Indexed it can used for implementing filters on 
 * fields that reference other DataObject tables. In this case it is a good idea to subclass
 * again so as to improve type safety and hide the field name.
 * <p>
 * 
 * @author spb
 * @param <BDO> type of factory
 * @param <R> indexed type
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: ReferenceFilter.java,v 1.10 2014/09/15 14:30:29 spb Exp $")

public final class ReferenceFilter< BDO extends DataObject,R extends Indexed> extends SQLValueFilter<BDO> implements SQLFilter<BDO>{


	/** Make the filter
     * 
     * @param field field referencing the peer
     * @param peer Indexed Object null for all records
     * @param factory factory filter is for
     */
    public ReferenceFilter(DataObjectFactory<BDO> factory, String field,R peer){
    	super(factory.getTarget(),factory.res,field,peer == null ? null : peer.getID());
    }
	
}