package uk.ac.ed.epcc.webapp.email;

import java.util.Date;

import uk.ac.ed.epcc.webapp.AbstractContexed;
import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.model.cron.HeartbeatListener;

public class QueuedMessageHeartbeatListener extends AbstractContexed implements HeartbeatListener {

	public static final Feature QUEUED_MESSAGE_HEARTBEAT = new Feature("queued_message.heartbeat", false, "Run queued messages in a heartbeat listener");
	public QueuedMessageHeartbeatListener(AppContext conn) {
		super(conn);
	}

	@Override
	public Date run() {
		if( ! QUEUED_MESSAGE_HEARTBEAT.isEnabled(getContext())) {
			return null;
		}
		QueuedMessages fac = QueuedMessages.getFactory(getContext());
		fac.retry();
		return null;
	}

}
