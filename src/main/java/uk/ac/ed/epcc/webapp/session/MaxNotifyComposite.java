package uk.ac.ed.epcc.webapp.session;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.CurrentTimeService;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.MessageResult;
import uk.ac.ed.epcc.webapp.forms.transition.AbstractDirectTransition;
import uk.ac.ed.epcc.webapp.forms.transition.Transition;
import uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.MatchCondition;
import uk.ac.ed.epcc.webapp.jdbc.filter.SQLAndFilter;
import uk.ac.ed.epcc.webapp.jdbc.table.DateFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.IntegerFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.data.Composite;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.Repository.Record;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.filter.FieldOrderFilter;
import uk.ac.ed.epcc.webapp.model.data.filter.SQLValueFilter;

/** A {@link Composite} on an {@link AppUser}
 * to record when required page notifications were last sent
 * 
 * @author Stephen Booth
 *
 * @param <A>
 */
public class MaxNotifyComposite<A extends AppUser> extends Composite<A, MaxNotifyComposite> implements LoginObserver<A> , AppUserTransitionContributor<A>{

	private static final String NOTIFY_COUNT_FIELD = "NotifyCount";
	private static final String LAST_NOTIFY__FIELD = "LastNotify";

	public MaxNotifyComposite(DataObjectFactory<A> fac) {
		super(fac);
	}

	@Override
	protected Class<? super MaxNotifyComposite> getType() {
		return MaxNotifyComposite.class;
	}

	@Override
	public TableSpecification modifyDefaultTableSpecification(TableSpecification spec, String table) {
		spec.setOptionalField(NOTIFY_COUNT_FIELD, new IntegerFieldType(false, 0));
		spec.setOptionalField(LAST_NOTIFY__FIELD, new DateFieldType(false, new Date(0L)));
		return spec;
	}

	@Override
	public Set<String> addSuppress(Set<String> suppress) {
		suppress.add(NOTIFY_COUNT_FIELD);
		suppress.add(LAST_NOTIFY__FIELD);
		return suppress;
	}
	/** How many times has the user been notified
	 * 
	 * @param user
	 * @return
	 */
	public int getNotifiedCount(A user) {
		return getRecord(user).getIntProperty(NOTIFY_COUNT_FIELD,0);
	}
	/** return the Date of the last notification
	 * (or null if the user has logged in since).
	 * 
	 * @param user
	 * @return
	 */
	public Date getLastNotified(A user) {
		if( getNotifiedCount(user) > 0) {
			return getRecord(user).getDateProperty(LAST_NOTIFY__FIELD);
		}
		return null;
	}
	public void setLastNotified(A user,Date t) {
		getRecord(user).setOptionalProperty(LAST_NOTIFY__FIELD, t);
	}
	
	
	/** record an additional notification sent to the user
	 * 
	 * @param user
	 * @throws DataFault
	 */
	public void addNotified(A user) throws DataFault {
		Record record = getRecord(user);
		record.setOptionalProperty(NOTIFY_COUNT_FIELD, getNotifiedCount(user)+1);
		CurrentTimeService time = getContext().getService(CurrentTimeService.class);
		if( time != null) {
			record.setOptionalProperty(LAST_NOTIFY__FIELD, time.getCurrentTime());
		}
		user.commit();
	}
	/** increase the notification count to the maximum to supress further notifications
	 * 
	 * @param user
	 * @throws DataFault
	 */
	public void stopNotified(A user) throws DataFault {
		getRecord(user).setOptionalProperty(NOTIFY_COUNT_FIELD, maxNotify());
		user.commit();
	}

	@Override
	public void userLoggedIn(A user) {
		// clear the record when user logs in
		// keep the last notified field as we still want to use that
		// for ordering first notifications.
		try {
			getRecord(user).setOptionalProperty(NOTIFY_COUNT_FIELD, 0);
			user.commit();
		} catch (DataFault e) {
			getLogger().error("Error setting notify count",e);
		}
		
	}
	public int maxNotify() {
		return getContext().getIntegerParameter("person.notify.max_count", 5);
	}
	public int minRepeatHours() {
		return getContext().getIntegerParameter("person.notify.min_repeat_hours", 24);
	}
	/** Should notifications be sent to the user
	 * 
	 * @param user
	 * @return
	 */
	public boolean sendNotifications(A user) {
		if( maxSent(user)) {
			return false;
		}
		if( recentNotification(user)) {
			return false;
		}
		return true;
	}
	public boolean recentNotification(A user) {
		int minRepeatHours = minRepeatHours();
		if( minRepeatHours <= 0) {
			return false;
		}
		// use raw field just in case we cleared without fixing the problem
		Date d = getRecord(user).getDateProperty(LAST_NOTIFY__FIELD);
		if( d != null && d.getTime() > 0L) {
			CurrentTimeService time = getContext().getService(CurrentTimeService.class);
			if( time != null ) {
				
				return (time.getCurrentTime().getTime() - (minRepeatHours * 3600000L)) < d.getTime() ;
			}
		}
		return false;
	}

	public boolean maxSent(A user) {
		return getNotifiedCount(user) >= maxNotify();
	}
	/** get a filter for who notifications should be sent to
	 * 
	 * @return
	 */
	public BaseFilter<A> getNotifyFilter(boolean include_rate_limit){
		if( ! apply()) {
			return null;
		}
		Class<A> target = getFactory().getTarget();
		SQLAndFilter<A> fil = new SQLAndFilter<A>(target);
		if( getRepository().hasField(NOTIFY_COUNT_FIELD)) {
		   fil.addFilter(new SQLValueFilter<A>(target,getRepository(),NOTIFY_COUNT_FIELD,MatchCondition.LT,maxNotify()));
		   // low notification count first
		   // if we limit the number of notifications sent we want those that were sent
		   // to move down the list for the next run
		   fil.addFilter(new FieldOrderFilter<A>(target, getRepository(), NOTIFY_COUNT_FIELD, false));
		}
		if( include_rate_limit    && getRepository().hasField(LAST_NOTIFY__FIELD)) {
			CurrentTimeService time = getContext().getService(CurrentTimeService.class);
			if( time != null ) {
				fil.addFilter(new SQLValueFilter<A>(target, getRepository(), LAST_NOTIFY__FIELD, MatchCondition.LT, 
				new Date(time.getCurrentTime().getTime() - (minRepeatHours() * 3600000L))));
				// same with date more recent sends have lower priority than
				// ones from the previous cycle.
				// first time reminders have the highest 
				fil.addFilter(new FieldOrderFilter<A>(target, getRepository(), LAST_NOTIFY__FIELD, false));
			}
			
		}
		return fil;
	}
	/** Should these rules be applied
	 * returns false if the necessary database fields are missing.
	 * 
	 * @return
	 */
	public boolean apply() {
		return getRepository().hasField(NOTIFY_COUNT_FIELD);
	}

	private static RoleAppUserKey STOP_NOTIFY = new RoleAppUserKey("StopNotifications","Stop automatic notifications of required updates for the user", "StopNotifications") {

		@Override
		protected boolean allowState(AppUser user) {
			AppUserFactory fac = user.getFactory();
			MaxNotifyComposite comp = (MaxNotifyComposite) fac.getComposite(MaxNotifyComposite.class);
			return ( comp != null && comp.sendNotifications(user));
		}
		
	};
	@Override
	public Map<AppUserKey<A>, Transition<A>> getTransitions(AppUserTransitionProvider<A> provider) {
		Map<AppUserKey<A>, Transition<A>> t = new HashMap<AppUserKey<A>, Transition<A>>();
		if( getRepository().hasField(NOTIFY_COUNT_FIELD)) {
			t.put(STOP_NOTIFY,new AbstractDirectTransition<A>() {

				@Override
				public FormResult doTransition(A target, AppContext c) throws TransitionException {
					try {
						stopNotified(target);
					} catch (DataFault e) {
						getLogger().error("Error stopping notifications", e);
					}
					return new MessageResult("notifications_stopped");
				}
			});
		}
		return t;
	}
	
	

}
