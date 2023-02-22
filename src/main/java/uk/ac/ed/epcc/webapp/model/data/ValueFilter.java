//| Copyright - The University of Edinburgh 2014                            |
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
		super( fac.res, field, cond, peer);
		
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
		super(fac.res, field, peer, negate);
	}

	/**
	 * @param target
	 * @param res
	 * @param field
	 * @param peer
	 */
	public ValueFilter(DataObjectFactory<BDO> fac, String field,
			Object peer) {
		super(fac.res,field, peer);
	}

}