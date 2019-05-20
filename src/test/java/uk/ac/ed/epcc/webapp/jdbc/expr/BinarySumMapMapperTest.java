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

public class BinarySumMapMapperTest extends DateTableTest {

	
	
	@Test
	public void testSumMillis() throws DataException, InvalidKeyException{
		DateTableFactory fac = new DateTableFactory(getContext());
		
		for (Operator op : Operator.values()) {
			Map<Number,Number> result = fac.getBinarySumMap(null, op);
			double sum[] = new double[24];
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(START_TIME);
			for(int i=0 ; i< N_RECORDS; i++){
				sum[c.get(Calendar.HOUR_OF_DAY)] += op.operate(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.YEAR)).doubleValue();
				c.add(Calendar.SECOND,STEP);
			}
			
			for( int i=0 ; i< 24 ; i ++){
				System.out.println(sum[i]);
				assertEquals(sum[i], result.get(i).doubleValue(), 0.003);
			}
		}
		
	}

}