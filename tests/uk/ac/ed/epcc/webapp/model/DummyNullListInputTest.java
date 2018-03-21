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
package uk.ac.ed.epcc.webapp.model;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.NullListInputTestCase;

/**
 * @author spb
 *
 */
public class DummyNullListInputTest extends NullListInputTestCase {

	/**
	 * 
	 */
	public DummyNullListInputTest() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.NullListInputTestCase#makeFactory(uk.ac.ed.epcc.webapp.AppContext)
	 */
	@Override
	public DataObjectFactory makeFactory(AppContext conn) {
		return new Dummy1.Factory(conn);
	}

}
