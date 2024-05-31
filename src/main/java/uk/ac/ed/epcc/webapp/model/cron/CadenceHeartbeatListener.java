package uk.ac.ed.epcc.webapp.model.cron;

import java.util.Calendar;
import java.util.Date;

import uk.ac.ed.epcc.webapp.AppContext;

/**
 * 
 */
public abstract class CadenceHeartbeatListener extends AbstractHeartbeatListener {

	public CadenceHeartbeatListener(AppContext conn) {
		super(conn);
	}

	/** Get the Calendar field type unit for the repeat
	 * 
	 * @return
	 */
	public int getCadenceField() {
		return Calendar.MINUTE;
	}
	/** Get the repeat frequency.
	 * 
	 * @return
	 */
	public abstract int getRepeat();

	@Override
	protected final Date nextRun(Date lastLocked) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(lastLocked);
		cal.add(getCadenceField(), getRepeat());
		return cal.getTime();
	}

	
}
