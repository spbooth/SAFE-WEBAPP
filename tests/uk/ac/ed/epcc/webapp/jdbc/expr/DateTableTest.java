//| Copyright - The University of Edinburgh 2012                            |
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
package uk.ac.ed.epcc.webapp.jdbc.expr;

import java.util.Calendar;

import org.junit.Before;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;

/**
 * @author spb
 *
 */

public abstract class DateTableTest extends WebappTestBase {

	/**
	 * 
	 */
	public static final int STEP = 3500;
	/**
	 * 
	 */
	public static final int N_RECORDS = 24*50;
	/**
	 * 
	 */
	public static final long START_TIME = 1355302800000L;


	

	
	@Before
	public void makeFixture() throws DataFault{
		DateTableFactory fac = new DateTableFactory(getContext());
		
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis( START_TIME);
		for(int i =0 ; i < N_RECORDS; i++){
			DateTable t = fac.create(c);
			t.commit();
			c.add(Calendar.SECOND,STEP);
		}
	}
}