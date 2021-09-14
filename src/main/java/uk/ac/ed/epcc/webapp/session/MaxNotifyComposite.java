package uk.ac.ed.epcc.webapp.session;

import java.util.Set;

import uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.MatchCondition;
import uk.ac.ed.epcc.webapp.jdbc.table.IntegerFieldType;
import uk.ac.ed.epcc.webapp.jdbc.table.TableSpecification;
import uk.ac.ed.epcc.webapp.model.data.Composite;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.filter.SQLValueFilter;

/** A {@link Composite} on an {@link AppUser}
 * to record when required page notifications were last sent
 * 
 * @author Stephen Booth
 *
 * @param <A>
 */
public class MaxNotifyComposite<A extends AppUser> extends Composite<A, MaxNotifyComposite> implements LoginObserver<A>{

	private static final String NOTIFY_COUNT_FIELD = "NotifyCount";

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
		return spec;
	}

	@Override
	public Set<String> addSuppress(Set<String> suppress) {
		suppress.add(NOTIFY_COUNT_FIELD);
		return suppress;
	}
	public int getNotifiedCount(A user) {
		return getRecord(user).getIntProperty(NOTIFY_COUNT_FIELD,0);
	}
	public void addNotified(A user) throws DataFault {
		getRecord(user).setOptionalProperty(NOTIFY_COUNT_FIELD, getNotifiedCount(user)+1);
		user.commit();
	}

	@Override
	public void userLoggedIn(A user) {
		// clear the record when user logs in
		
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
	public boolean sendNotifications(A user) {
		return getNotifiedCount(user) < maxNotify();
	}
	public BaseFilter<A> getNotifyFilter(){
		if( ! apply()) {
			return null;
		}
		return new SQLValueFilter<A>(getFactory().getTarget(),getRepository(),NOTIFY_COUNT_FIELD,MatchCondition.LT,maxNotify());
	}

	public boolean apply() {
		return getRepository().hasField(NOTIFY_COUNT_FIELD);
	}
	
	

}
