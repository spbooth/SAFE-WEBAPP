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
import uk.ac.ed.epcc.webapp.model.cron.CadenceHeartbeatListener;
import uk.ac.ed.epcc.webapp.model.cron.HeartbeatListener;
import uk.ac.ed.epcc.webapp.model.cron.LockFactory;
import uk.ac.ed.epcc.webapp.model.cron.LockFactory.Lock;
import uk.ac.ed.epcc.webapp.model.data.FilterResult;

/** a {@link HeartbeatListener} to send required page notifications
 * @author Stephen Booth
 *
 */
public class RequiredPageNotifyHearbeatListener<AU extends AppUser> extends CadenceHeartbeatListener {
    public static final Feature REQUIRED_PAGE_HEARTBEAT = new Feature("required_page.heartbeat",true,"Send emails to notify users of required actions from heartbeat");
    
    public static final Feature REQUIRED_PAGE_ACTIONS = new Feature("required_page.heartbeat.actions",true,"Apply side effects if users do not complete required page actions");
    /**
	 * @param conn
	 */
	public RequiredPageNotifyHearbeatListener(AppContext conn) {
		super(conn);
	}

	
	


	@Override
	protected boolean enabled() {
		return  REQUIRED_PAGE_HEARTBEAT.isEnabled(getContext());
	}


	

	@Override
	public int getCadenceField() {
		return Calendar.HOUR_OF_DAY;
	}
	@Override
	public int getRepeat() {
		return getContext().getIntegerParameter("required_page.heartbeat.hours", 48);
	}


	@Override
	protected String getLockName() {
		return "RequiredPageListener";
	}


	@Override
	public void process() {
		SessionService<AU> sess = getContext().getService(SessionService.class);
		Logger logger = getLogger();
		if(sess == null ) {
			logger.error("No session service");
			return;
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
			try {
				logger.debug("Applying actions");
				pol.applyActions();
			}catch(Exception e) {
				logger.error("Error applying required page actions",e);
			}
		}
		
	}

	

}
