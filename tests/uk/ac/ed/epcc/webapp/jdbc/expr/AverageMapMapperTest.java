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

public class AverageMapMapperTest extends DateTableTest {

	@Test
	public void testAvgMillis() throws DataException{
		DateTableFactory fac = new DateTableFactory(getContext());
		
		Map<Integer,Number> result = fac.getAvgMap(null);
		long sum[] = new long[24];
		int count[] = new int[24];
		double avg[] = new double[24];
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(START_TIME);
		for(int i=0 ; i< N_RECORDS; i++){
			sum[c.get(Calendar.HOUR_OF_DAY)] += c.getTimeInMillis();
			count[c.get(Calendar.HOUR_OF_DAY)] ++;
			c.add(Calendar.SECOND,STEP);
		}
		
		for( int i=0 ; i< 24 ; i ++){
			avg[i] = (double) sum[i] / (double) count[i];
			System.out.print("avg["+i+"]=");
			System.out.println(avg[i]);

			assertEquals(avg[i], result.get(i).doubleValue(), 0.0003);
		}
		
	}

}