package uk.ac.ed.epcc.webapp.session;

import java.util.Set;

import uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter;

public interface RequiredPageAction<U extends AppUser> {

	/** Get a {@link BaseFilter} for {@link AppUser}s which meet the condition for the action
	 * to be applied.
	 * Ideally this will not match where the action has already been applied.
	 * 
	 */
	public BaseFilter<U> triggerFilter(SessionService<U> sess);
	
	/** apply the actions
	 * 
	 * @param user
	 */
	public void applyAction(U user);
	
	/** If side effects are configured add notification text explaining the actions to be taken.
	 * These will be added to automatic emails requesting changes so an action should not
	 * be added unless the corresponding requirement is added via {@link #addNotifyText(Set, AppUser)}
	 * @param notices
	 * @param person
	 */
	default public void addActionText(Set<String> notices,U person) {
		
	}
}
