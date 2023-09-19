package uk.ac.ed.epcc.webapp.email;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.model.cron.EagerHeartbeatListener;

public class QueuedMessageHeartbeatListener extends EagerHeartbeatListener {

	public static final Feature QUEUED_MESSAGE_HEARTBEAT = new Feature("queued_message.heartbeat", false, "Run queued messages in a heartbeat listener");
	public QueuedMessageHeartbeatListener(AppContext conn) {
		super(conn);
	}

	@Override
	protected boolean enabled() {
		return QUEUED_MESSAGE_HEARTBEAT.isEnabled(getContext());
	}

	@Override
	protected String getLockName() {
		return "QueuedMessagesHeartbeat";
	}

	@Override
	public void process() {
		QueuedMessages fac = QueuedMessages.getFactory(getContext());
		fac.retry();
	}

}
