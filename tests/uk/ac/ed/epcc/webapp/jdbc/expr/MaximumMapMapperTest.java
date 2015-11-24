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
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.2 $")
public class MaximumMapMapperTest extends DateTableTest {

	
	
	@Test
	public void testMaxMillis() throws DataException{
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
