//| Copyright - The University of Edinburgh 2011                            |
//|                                                                         |
//| Licensed under the Apache License, Version 2.0 (the "License");         |
//| you may not use this file except in compliance with the License.        |
//| You may obtain a copy of the License at                                 |
//|                                                                         |
//|    http://www.apache.org/licenses/LICENSE-2.0                           |
//|                                                                         |
//| Unless required by applicable law or agreed to in writing, software     |
//| distributed under the License is distributed on an "AS IS" BASIS,       |
//| WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.|
//| See the License for the specific language governing permissions and     |
//| limitations under the License.                                          |
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