// Copyright - The University of Edinburgh 2012
package uk.ac.ed.epcc.webapp.forms.transition;

import uk.ac.ed.epcc.webapp.session.SessionService;

/**
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.3 $")
public interface ViewTransitionFactoryDataProvider<K,T> extends
		TransitionFactoryDataProvider<K, T> {
	public ViewTransitionFactory<K, T> getTransitionFactory();
	
	/** user allowed access to target
	 * 
	 * @param target
	 * @return SessionService
	 * @throws Exception 
	 */
	public SessionService<?> getAllowedUser(T target) throws Exception;
	
	/** User not allowed access to target.
	 * 
	 * @param target
	 * @return SessionService or null to skip test
	 * @throws Exception 
	 */
	public SessionService<?> getForbiddenUser(T target) throws Exception;
}
