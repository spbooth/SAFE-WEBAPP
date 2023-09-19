//| Copyright - The University of Edinburgh 2015                            |
//|                                                                         |
//| Licensed under the Apache License, Version 2.0 (the "License");         |
//| you may not use this file except in compliance with the License.        |
//| You may obtain a copy of the License at                                 |
//|                                                                         |
//|    http://www.apache.org/licenses/LICENSE-2.0                           |
//|                                                                         |
//| Unless required by applicable law or agreed to in writing, software     |
//| distributed under the License is distributed on an "AS IS" BASIS,       |
//| WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.|
//| See the License for the specific language governing permissions and     |
//| limitations under the License.                                          |
package uk.ac.ed.epcc.webapp.model.serv;

import java.util.Calendar;
import java.util.Date;

import uk.ac.ed.epcc.webapp.AbstractContexed;
import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.CurrentTimeService;
import uk.ac.ed.epcc.webapp.model.cron.CadenceHeartbeatListener;
import uk.ac.ed.epcc.webapp.model.cron.HeartbeatListener;
import uk.ac.ed.epcc.webapp.model.cron.LockFactory;
import uk.ac.ed.epcc.webapp.model.cron.LockFactory.Lock;

/** A {@link HeartbeatListener} that runs {@link DataObjectDataProducer#clean()} once a day.
 * This defaults to cleaning the default producer but this can be customised by putting a comma seperated list of
 * tags in the property cer.clean_tags</b>
 * @author spb
 *
 */

public class DataProducerHeartbeatListener extends CadenceHeartbeatListener {

	public DataProducerHeartbeatListener(AppContext conn) {
		super(conn);
	}
	
	@Override
	public int getRepeat() {
		return 1;
	}
	@Override
	public int getCadenceField() {
		return Calendar.HOUR_OF_DAY;
	}
	@Override
	protected String getLockName() {
		return "DataProducerHeartbeatListener";
	}
	@Override
	public void process() {
		String tags = getContext().getInitParameter("data_producer.clean_tags",ServeDataProducer.DEFAULT_SERVE_DATA_TAG);
		for(String tag : tags.split("\\s*,\\s*")){
			DataObjectDataProducer prod = new DataObjectDataProducer(getContext(), tag);
			if( prod != null ){
				prod.clean();
			}
		}
		
	}
}