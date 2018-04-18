//| Copyright - The University of Edinburgh 2013                            |
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
package uk.ac.ed.epcc.webapp.jdbc.table;

import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.Repository;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** 
 * @author spb
 *
 */

public class DefaultTableTransitionRegistry<X extends DataObjectFactory> extends AbstractTableRegistry {

	/**
	 * 
	 */
	public DefaultTableTransitionRegistry(Repository res,TableSpecification spec) {
		addTransitionSource(new GeneralTransitionSource<X>(res));
		if( spec != null ){
			addTransitionSource(new TableSpecificationTransitionSource<X>(res, spec));
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.jdbc.table.TableTransitionRegistry#getTableTransitionSummary(uk.ac.ed.epcc.webapp.content.ContentBuilder, uk.ac.ed.epcc.webapp.session.SessionService)
	 */
	public void getTableTransitionSummary(ContentBuilder hb,
			SessionService operator) {
		return;

	}

}