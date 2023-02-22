package uk.ac.ed.epcc.webapp.session;

import java.util.Set;

import uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter;
/** Interface for side-effect actions applied when a user fails to visit the {@link RequiredPage}.
 * This can be implemented by the {@link RequiredPage} itself (a {@link RequiredPageWithAction}
 * or it might be a nested class that focuses on a particular set of actions to perform.
 * 
 * @author Stephen Booth
 *
 * @param <U>
 */
public interface RequiredPageAction<U extends AppUser> {

	/** Get a {@link BaseFilter} for {@link AppUser}s which meet the condition for the action
	 * to be applied.
	 * Ideally this will not match where the action has already been applied.
	 * For a {@link RequiredPageWithAction} this should include the rules to select based on the 
	 * {@link AppUser}s nested {@link RequiredPageAction}s need only filter for
	 * {@link AppUser}s where it is possible to apply the action
	 */
	public BaseFilter<U> triggerFilter(SessionService<U> sess);
	
	/** apply the actions
	 * 
	 * @param user
	 */
	public void applyAction(U user);
	
	/** If side effects are configured add notification text explaining the actions to be taken.
	 * These will be added to automatic emails requesting changes so an action should not
	 * be added unless the corresponding requirement is added.
	 * @param notices
	 * @param person
	 */
	default public void addActionText(Set<String> notices,U person) {
		
	}
}
