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

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Map;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;

/**
 * @author spb
 *
 */

public class MaximumMapMapperTest extends DateTableTest {

	
	
	@Test
	public void testMaxMillis() throws DataException, InvalidKeyException{
		DateTableFactory fac = new DateTableFactory(getContext());
		
		Map<Integer, Number> result = fac.getMaxMap(null);
		long max[] = new long[24];
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(START_TIME);
		for(int i=0 ; i< N_RECORDS; i++){
			long m = c.getTimeInMillis();
			
			int hour = c.get(Calendar.HOUR_OF_DAY);
			if( max[hour] == 0L || max[hour] < m){
				max[hour] = m;
			}
			c.add(Calendar.SECOND,STEP);
		}
		
		for( int i=0 ; i< 24 ; i ++){
			System.out.println(max[i]);
			assertEquals(max[i], result.get(i).longValue());
		}
		
	}

}