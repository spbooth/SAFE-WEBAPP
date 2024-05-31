package uk.ac.ed.epcc.webapp.model.cron;

import java.util.Date;

import uk.ac.ed.epcc.webapp.AppContext;

/** A {@link HeartbeatListener} that runs at every possible opportunity using a lock object.
 * 
 */
public abstract class EagerHeartbeatListener extends AbstractHeartbeatListener {

	public EagerHeartbeatListener(AppContext conn) {
		super(conn);
	}

	
	@Override
	protected final Date nextRun(Date lastLocked) {
		return null;
	}

	
}
