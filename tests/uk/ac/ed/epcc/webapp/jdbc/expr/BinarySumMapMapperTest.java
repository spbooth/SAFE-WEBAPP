// Copyright - The University of Edinburgh 2012
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
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.4 $")
public class BinarySumMapMapperTest extends DateTableTest {

	
	
	@Test
	public void testSumMillis() throws DataException{
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
