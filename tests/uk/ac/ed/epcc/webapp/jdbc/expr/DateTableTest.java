// Copyright - The University of Edinburgh 2012
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
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.2 $")
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
