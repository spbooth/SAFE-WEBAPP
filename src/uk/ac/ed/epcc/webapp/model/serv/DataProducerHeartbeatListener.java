// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.model.serv;

import java.util.Calendar;
import java.util.Date;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.model.cron.HeartbeatListener;

/** A {@link HeartbeatListener} that runs {@link DataObjectDataProducer#clean()} once a day.
 * This defaults to cleaning the default producer but this can be customised by putting a comma seperated list of
 * tags in the property cer.clean_tags</b>
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.1 $")
public class DataProducerHeartbeatListener implements Contexed, HeartbeatListener {

	public DataProducerHeartbeatListener(AppContext conn) {
		super();
		this.conn = conn;
	}
	private final AppContext conn;
	private Date next_run=null;
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.cron.HeartbeatListener#run()
	 */
	@Override
	public Date run() {
		Date now = new Date();
		if( next_run == null || next_run.before(now)){
			String tags = getContext().getInitParameter("data_producer.clean_tags",ServeDataProducer.DEFAULT_SERVE_DATA_TAG);
			for(String tag : tags.split("\\s*,\\s*")){
				DataObjectDataProducer prod = new DataObjectDataProducer(getContext(), tag);
				if( prod != null ){
					prod.clean();
				}
			}
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_YEAR, 1);
			next_run = cal.getTime();
		}
		return next_run;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Contexed#getContext()
	 */
	@Override
	public AppContext getContext() {
		return conn;
	}

}
