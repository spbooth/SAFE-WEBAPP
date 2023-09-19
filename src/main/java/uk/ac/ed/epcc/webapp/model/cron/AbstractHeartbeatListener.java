package uk.ac.ed.epcc.webapp.model.cron;

import java.util.Calendar;
import java.util.Date;

import uk.ac.ed.epcc.webapp.AbstractContexed;
import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.CurrentTimeService;
import uk.ac.ed.epcc.webapp.model.cron.LockFactory.Lock;
/** Abstract class for implementing {@link HeartbeatListener}.
 * 
 * Each instance uses its own lock-object and runs on a regular cadence
 * 
 */
public abstract class AbstractHeartbeatListener extends AbstractContexed implements HeartbeatListener {

	public AbstractHeartbeatListener(AppContext conn) {
		super(conn);
	}
	
	/** Name of the lock object to take.
	 * 
	 * @return
	 */
	protected abstract String getLockName();

	/** Calculate the date of the next run of this listener based 
	 * on the last one. Return null to run at all oppertunities.
	 * 
	 * @param lastLocked
	 * @return
	 */
	protected abstract Date nextRun(Date lastLocked); 
	
	/** Extension point to check for features/etc that disable this listener
	 * 
	 * @return
	 */
	protected boolean enabled() {
		return true;
	}
	
	/** Actually perform the listener actions.
	 * 
	 */
	public abstract void process();
	
	@Override
	public final Date run() {
		Date now = conn.getService(CurrentTimeService.class).getCurrentTime();
		try {
			LockFactory lock_f = LockFactory.getFactory(getContext());
			Lock lock = lock_f.makeFromString(getLockName());
			Calendar cal = Calendar.getInstance();
			if( lock.isLocked()) {
				Date d = lock.wasLockedAt();
				cal.setTime(d);
				cal.add(Calendar.HOUR,1);
				if(now.after(cal.getTime())) {
					getLogger().error(lock.getName()+" locked since "+d);
				}
				return null;
			}
			Date lastLocked = lock.lastLocked();
			if( lastLocked != null ) {
				Date target = nextRun(lastLocked);
				if( target != null && target.after(now)) {
					return target;
				}
			}
			if( ! enabled()) {
				return null;
			}
			
			if( lock.takeLock()) {
				try {
					process();
				}finally {
					lock.releaseLock();
				}
				return nextRun(now);
			}
			
		}catch(Exception e) {
			getLogger().error("Error taking lock",e);
		}
		return null;
	}

}
