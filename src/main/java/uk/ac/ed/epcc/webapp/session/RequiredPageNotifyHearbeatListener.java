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

import org.apache.logging.log4j.core.pattern.MaxLengthConverter;

import uk.ac.ed.epcc.webapp.AbstractContexed;
import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.CurrentTimeService;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.email.Emailer;
import uk.ac.ed.epcc.webapp.jdbc.filter.AndFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.OrFilter;
import uk.ac.ed.epcc.webapp.model.cron.HeartbeatListener;
import uk.ac.ed.epcc.webapp.model.cron.LockFactory;
import uk.ac.ed.epcc.webapp.model.cron.LockFactory.Lock;

/**
 * @author Stephen Booth
 *
 */
public class RequiredPageNotifyHearbeatListener<AU extends AppUser> extends AbstractContexed implements HeartbeatListener {
    public static final Feature REQUIRED_PAGE_HEARTBEAT = new Feature("required_page.heartbeat",true,"Send emails to notify users of required actions from heartbeat");
    public static final Feature REQUIRE_VERIFIED = new Feature("required_page.heartbeat.require_verified",true,"Require email address has been verified at least once before sending email");
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
					getLogger().error("Lock "+lock.getName()+" held since "+locked);
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
				getLogger().error("No session service");
				return null;
			}

			AppUserFactory<AU> login = sess.getLoginFactory();
			MaxNotifyComposite<AU> max = login.getComposite(MaxNotifyComposite.class);
			OrFilter<AU> fil = new OrFilter<AU>(login.getTarget(), login);
			Set<RequiredPage<AU>> requiredPages = login.getRequiredPages();
			for(RequiredPage<AU> rp : requiredPages) {
				fil.addFilter(rp.notifiable(sess));
			}
			Emailer mailer = new Emailer(getContext());
			AndFilter<AU> notify_filter = new AndFilter<AU>(login.getTarget(), fil, login.getEmailFilter(), login.getCanLoginFilter());
			if( max != null) {
				notify_filter.addFilter(max.getNotifyFilter());
			}
			EmailNameFinder<AU> finder = login.getComposite(EmailNameFinder.class);
			if( finder != null && REQUIRE_VERIFIED.isEnabled(getContext())) {
				// Don't send emails to an address that has never been verified
				notify_filter.addFilter(finder.getIsVerifiedFilter());
			}
			try {
				for( AU person : login.getResult(notify_filter)) {
					// Don't notify a user who can't login to fix
					if( person.canLogin() && person.allowEmail() && (max == null || max.sendNotifications(person))) {
						try {
							Set<String> notices = new LinkedHashSet<String>();
							for(RequiredPage<AU> rp : requiredPages) {
								rp.addNotifyText(notices,person);
							}
							if( ! notices.isEmpty()) {
								if( max != null) {
									max.addNotified(person);
								}
								mailer.notificationEmail(person, notices);
							}
						}catch(Exception em) {
							getLogger().error("Error sending required page notifications to "+person.getEmail(), em);
						}
					}
				}
			} catch (Exception e) {
				getLogger().error("Error generating notification emails", e);
			}

			lock.releaseLock();
			Calendar c = Calendar.getInstance();
			c.setTime(time.getCurrentTime());
			c.add(Calendar.HOUR,hours);
			
			return c.getTime();
		} catch (Exception e) {
			getLogger().error("Error in required page listener",e);
			return null;
		}
	}

	

}
