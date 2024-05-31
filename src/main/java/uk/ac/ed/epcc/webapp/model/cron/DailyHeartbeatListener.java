package uk.ac.ed.epcc.webapp.model.cron;

import java.util.Calendar;
import java.util.Date;

import uk.ac.ed.epcc.webapp.AppContext;

/** A {@link HeartbeatListener} that runs at the same time every day.
 * 
 */
public abstract class DailyHeartbeatListener extends AbstractHeartbeatListener {

	public DailyHeartbeatListener(AppContext conn) {
		super(conn);
	}

	/** Get the repeat hour.
	 * 
	 * @return
	 */
	public abstract int getRepeatHour();
	
	public int getRepeatMin() {
		return 0;
	}
	public int getRepeatSecond() {
		return 0;
	}

	@Override
	protected final Date nextRun(Date lastLocked) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(lastLocked);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, getRepeatSecond());
		cal.set(Calendar.MINUTE, getRepeatMin());
		cal.set(Calendar.HOUR, getRepeatHour());
		cal.add(Calendar.DAY_OF_YEAR, 1);
		return cal.getTime();
	}

	
}
