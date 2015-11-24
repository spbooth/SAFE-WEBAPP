// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp;

import java.util.Date;

/** A {@link CurrentTimeService}
 * that allows the time to be set for tests.
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.1 $")
public class TestTimeService extends CurrentTimeService {
  /* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.CurrentTimeService#getCurrentTime()
	 */
	@Override
	public Date getCurrentTime() {
		if( result != null ){
			return result;
		}
		return super.getCurrentTime();
	}

private Date result;

/**
 * @return the result
 */
public Date getResult() {
	return result;
}

/**
 * @param result the result to set
 */
public void setResult(Date result) {
	this.result = result;
}
  
  
}
