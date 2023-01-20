//| Copyright - The University of Edinburgh 2015                            |
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
package uk.ac.ed.epcc.webapp.model.data.filter;

import uk.ac.ed.epcc.webapp.Indexed;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLOrFilter;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.ReferenceFilter;
import uk.ac.ed.epcc.webapp.model.data.Repository;
/** Like {@link ReferenceFilter} but also matches objects
 * where the reference field is null
 * 
 * @author spb
 *
 * @param <T>
 * @param <P>
 */
public class WildCardReferenceFilter<T extends DataObject,P extends Indexed> extends SQLOrFilter<T> {

	public WildCardReferenceFilter(Repository res, String field, P peer){
		super(res.getTag());
		addFilter(new NullFieldFilter<>(res, field, true));
		if( peer != null){
			addFilter(new SQLValueFilter<>(res, field, peer.getID()));
		}
	}
}