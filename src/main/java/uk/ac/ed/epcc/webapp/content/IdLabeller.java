//| Copyright - The University of Edinburgh 2018                            |
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
package uk.ac.ed.epcc.webapp.content;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Indexed;
import uk.ac.ed.epcc.webapp.model.data.reference.IndexedReference;

/** Label with the raw-ids
 * @author Stephen Booth
 *
 */
public class IdLabeller<X extends Indexed> implements Labeller<IndexedReference<X>, Integer> {

	/**
	 * 
	 */
	public IdLabeller() {

	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Targetted#getTarget()
	 */
	@Override
	public Class<Integer> getTarget() {
		return Integer.class;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.Labeller#getLabel(uk.ac.ed.epcc.webapp.AppContext, java.lang.Object)
	 */
	@Override
	public Integer getLabel(AppContext conn, IndexedReference<X> key) {
		return key.getID();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.Labeller#accepts(java.lang.Object)
	 */
	@Override
	public boolean accepts(Object o) {
		return o instanceof IndexedReference;
	}

}
