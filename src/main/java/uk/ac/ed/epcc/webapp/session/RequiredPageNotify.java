package uk.ac.ed.epcc.webapp.session;

import java.util.Set;

import uk.ac.ed.epcc.webapp.AbstractContexed;
import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.jdbc.filter.AndFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.FalseFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.OrFilter;
import uk.ac.ed.epcc.webapp.model.data.NamedFilterWrapper;
/** Policy object for required page notifications
 * 
 * @author Stephen Booth
 *
 */
public class RequiredPageNotify<AU extends AppUser> extends AbstractContexed {

	public static final String REQUIRED_PAGE_POLICY_TAG = "RequiredPagePolicy";
	public static final Feature REQUIRE_VERIFIED = new Feature("required_page.heartbeat.require_verified",true,"Require email address has been verified at least once before sending email");

	public static RequiredPageNotify getPolicy(AppContext conn) {
		return conn.makeObject(RequiredPageNotify.class, REQUIRED_PAGE_POLICY_TAG);
	}
	private final SessionService<AU> sess;
	private final AppUserFactory<AU> login;
	private final Set<RequiredPage<AU>> requiredPages;
	private final MaxNotifyComposite<AU> max;
	private final String policy_role;
	public RequiredPageNotify(AppContext conn) {
		super(conn);
		sess = getContext().getService(SessionService.class);
		login = sess.getLoginFactory();
		requiredPages = login.getRequiredPages();
		max = login.getComposite(MaxNotifyComposite.class);
		policy_role = getContext().getInitParameter("required_page.notifications.role");
	}

	public BaseFilter<AU> getNotifyFilter(boolean apply_rate_limit){ 
		if(sess == null ) {
			getLogger().error("No session service");
			return new FalseFilter<AU>(login.getTarget());
		}

		
		OrFilter<AU> fil = new OrFilter<AU>(login.getTarget(), login);
		for(RequiredPage<AU> rp : requiredPages) {
			fil.addFilter(rp.notifiable(sess));
		}
		AndFilter<AU> notify_filter = new AndFilter<AU>(login.getTarget(), fil, login.getEmailFilter(), login.getCanLoginFilter());
		if( max != null && apply_rate_limit) {
			notify_filter.addFilter(max.getNotifyFilter());
		}
		
		if( policy_role != null && ! policy_role.isEmpty()) {
			
			// add a named filter to specify who should receive notifications
			notify_filter.addFilter(sess.getGlobalRoleFilter(policy_role));
		}
		EmailNameFinder<AU> finder = login.getComposite(EmailNameFinder.class);
		if( finder != null && RequiredPageNotify.REQUIRE_VERIFIED.isEnabled(getContext())) {
			// Don't send emails to an address that has never been verified
			notify_filter.addFilter(finder.getIsVerifiedFilter());
		}
		return notify_filter;
	}
	
	public boolean allow(AU person, boolean apply_rate_limit) {
		return person.canLogin() && person.allowEmail() && (max == null || ! apply_rate_limit ||  max.sendNotifications(person)) && ( policy_role == null|| sess.canHaveRole(person, policy_role));
	}

	public Set<RequiredPage<AU>> getRequiredPages() {
		return requiredPages;
	}

	public MaxNotifyComposite<AU> getMax() {
		return max;
	}
 }
