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

import java.util.*;

import uk.ac.ed.epcc.webapp.*;
import uk.ac.ed.epcc.webapp.email.Emailer;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.model.cron.HeartbeatListener;
import uk.ac.ed.epcc.webapp.model.cron.LockFactory;
import uk.ac.ed.epcc.webapp.model.cron.LockFactory.Lock;
import uk.ac.ed.epcc.webapp.model.data.FilterResult;

/** a {@link HeartbeatListener} to send required page notifications
 * @author Stephen Booth
 *
 */
public class RequiredPageNotifyHearbeatListener<AU extends AppUser> extends AbstractContexed implements HeartbeatListener {
    public static final Feature REQUIRED_PAGE_HEARTBEAT = new Feature("required_page.heartbeat",true,"Send emails to notify users of required actions from heartbeat");
    
    public static final Feature REQUIRED_PAGE_ACTIONS = new Feature("required_page.heartbeat.actions",true,"Apply side effects if users do not complete required page actions");
    /**
	 * @param conn
	 */
	public RequiredPageNotifyHearbeatListener(AppContext conn) {
		super(conn);
	}

	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.cron.HeartbeatListener#run()
	 */
	@Override
	public Date run() {
		if( ! REQUIRED_PAGE_HEARTBEAT.isEnabled(getContext())) {
			return null;
		}
		CurrentTimeService time = getContext().getService(CurrentTimeService.class);
		Date now = time.getCurrentTime();
		int hours = getContext().getIntegerParameter("required_page.heartbeat.hours", 48);
		Calendar thresh = Calendar.getInstance();
		thresh.setTime(now);
		thresh.add(Calendar.HOUR, - hours);

		LockFactory locks = LockFactory.getFactory(getContext());
		Logger logger = getLogger();
		try(Lock lock = locks.makeFromString("RequiredPageListener")){
			if( lock == null) {
				return null;
			}
			Date last = lock.lastLocked();
			Calendar next = Calendar.getInstance();
			next.setTime(now);
			if( last != null) {
				next.setTime(last);
			}
			next.add(Calendar.HOUR, hours);

			if( lock.isLocked()) {
				Date locked = lock.wasLockedAt();
				if( locked.getTime()+60000L < time.getCurrentTime().getTime()) {
					logger.error("Lock "+lock.getName()+" held since "+locked);
				}
				return next.getTime();
			}
			if( last != null && last.after(thresh.getTime())) {
				return next.getTime();
			}
			if( ! lock.takeLock()) {
				return next.getTime();
			}
			
			SessionService<AU> sess = getContext().getService(SessionService.class);
			if(sess == null ) {
				logger.error("No session service");
				return null;
			}
			RequiredPageNotify<AU> pol = RequiredPageNotify.getPolicy(getContext());
 			AppUserFactory<AU> login = sess.getLoginFactory();
 			MaxNotifyComposite<AU> max = pol.getMax();
 			int max_send = getContext().getIntegerParameter("required_page.notifications.max_batch", 50);
			Emailer mailer = Emailer.getFactory(getContext());
			try(FilterResult<AU> res = login.getResult(pol.getNotifyFilter(true))) {
				int count=0;
				for( AU person : res) {
					logger.debug(Integer.toString(count)+": Required page notifications for "+person.getIdentifier());
					// Don't notify a user who can't login to fix
					// these rules should also be in the filter.
					if( pol.allow(person, true)) {
						if( max_send >0 && count >= max_send) {
							logger.debug("Terminating notify due to max_send");
							break;
						}
						count++;
						try {
							Set<String> notices = new LinkedHashSet<String>();
							Set<String> actions = new LinkedHashSet<String>();
							for(RequiredPage<AU> rp : pol.getRequiredPages()) {
								rp.addNotifyText(notices,person);
								if(REQUIRED_PAGE_ACTIONS.isEnabled(getContext()) && rp instanceof RequiredPageWithAction) {
									((RequiredPageWithAction<AU>)rp).addActionText(actions, person);
								}
							}
							if( ! notices.isEmpty()) {
								logger.debug("Sending notification");
								if( max != null) {
									max.addNotified(person);
									logger.debug("addNotified");
								}
								mailer.notificationEmail(person, notices,actions);
							}
						}catch(Exception em) {
							logger.error("Error sending required page notifications to "+person.getEmail(), em);
						}
					}else {
						logger.debug("Not allowed by policy");
					}
				}
				logger.debug("Notifications sent :"+count);
			} catch (Exception e) {
				logger.error("Error generating notification emails", e);
			}
			
			if( REQUIRED_PAGE_ACTIONS.isEnabled(getContext())) {
				logger.debug("Applying actions");
				pol.applyActions();
			}
			lock.releaseLock();
			Calendar c = Calendar.getInstance();
			c.setTime(time.getCurrentTime());
			c.add(Calendar.HOUR,hours);
			
			return c.getTime();
		} catch (Exception e) {
			logger.error("Error in required page listener",e);
			return null;
		}
	}

	

}
