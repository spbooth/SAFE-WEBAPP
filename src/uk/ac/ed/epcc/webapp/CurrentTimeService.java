// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp;

import java.util.Date;

/** An {@link AppContextService} that returns the current time.
 * This is to allow tests to override the service to supply a different time.
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.1 $")
public class CurrentTimeService implements AppContextService<CurrentTimeService> {

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.AppContextService#cleanup()
	 */
	@Override
	public void cleanup() {
		
	}

	public Date getCurrentTime(){
		return new Date();
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.AppContextService#getType()
	 */
	@Override
	public Class<? super CurrentTimeService> getType() {
		return CurrentTimeService.class;
	}

}
