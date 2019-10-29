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
package uk.ac.ed.epcc.webapp.model.data.reference;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Indexed;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.model.data.DataCache;
/** A DataCache keyed by IndexedReference objects.
 * This needs no additional logic to create the target objects as the IndexedRefenece can do this 
 * directly. However the use of a cache can reduce the number of database lookups.
 * 
 * @author spb
 *
 * @param <I>
 */


public class IndexedReferenceDataCache<I extends Indexed> extends DataCache<IndexedReference<? extends I>, I> {
    private AppContext c;
    public IndexedReferenceDataCache(AppContext c){
    	this.c=c;
    }
	@Override
	protected I find(IndexedReference<? extends I> key) throws DataException {
		return key.getIndexed(c);
	}

}