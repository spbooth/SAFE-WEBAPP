//| Copyright - The University of Edinburgh 2020                            |
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
package uk.ac.ed.epcc.webapp.session;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import uk.ac.ed.epcc.webapp.AbstractContexed;
import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.CurrentTimeService;
import uk.ac.ed.epcc.webapp.email.Emailer;
import uk.ac.ed.epcc.webapp.jdbc.filter.AndFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.OrFilter;
import uk.ac.ed.epcc.webapp.model.cron.HeartbeatListener;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;

/**
 * @author Stephen Booth
 *
 */
public class RequiredPageNotifyHearbeatListener<AU extends AppUser> extends AbstractContexed implements HeartbeatListener {

	/**
	 * @param conn
	 */
	public RequiredPageNotifyHearbeatListener(AppContext conn) {
		super(conn);
	}

	private static Date target=null;
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.cron.HeartbeatListener#run()
	 */
	@Override
	public Date run() {
		CurrentTimeService time = getContext().getService(CurrentTimeService.class);
		Date now = time.getCurrentTime();
		
		synchronized(getClass()) {
			if( target != null && target.after(now)){
				return target;
			}
		}
		SessionService<AU> sess = getContext().getService(SessionService.class);
		if(sess == null ) {
			getLogger().error("No session service");
			return null;
		}
		
		AppUserFactory<AU> login = sess.getLoginFactory();
		OrFilter<AU> fil = new OrFilter<AU>(login.getTarget(), login);
		Set<RequiredPage<AU>> requiredPages = login.getRequiredPages();
		for(RequiredPage<AU> rp : requiredPages) {
			fil.addFilter(rp.notifiable(sess));
		}
		Emailer mailer = new Emailer(getContext());
		AndFilter<AU> notify_filter = new AndFilter<AU>(login.getTarget(), fil, login.getEmailFilter());
		try {
			for( AU person : login.getResult(notify_filter)) {
				Set<String> notices = new LinkedHashSet<String>();
				for(RequiredPage<AU> rp : requiredPages) {
					String t = rp.getNotifyText(person);
					if( t!=null && ! t.isEmpty()) {
						notices.add(t);
					}
				}
				if( ! notices.isEmpty()) {
					mailer.notificationEmail(person, notices);
				}
			}
		} catch (Exception e) {
			getLogger().error("Error generating notification emails", e);
		}
		
		Calendar c = Calendar.getInstance();
		c.setTime(time.getCurrentTime());
		c.add(Calendar.HOUR,getContext().getIntegerParameter("required_page.heartbeat.hours", 24));
		target=c.getTime();
		return c.getTime();
	}
	
	public static void reset() {
		target=null;
	}

}
