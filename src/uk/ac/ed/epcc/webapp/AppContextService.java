// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp;

/** Interface for Services stored in an AppContext. 
 * 
 * Though the implementation of the services can form a class hierarchy
 * the target services should not, as each one should be registered under a unique 
 * service. 
 * 
 * Service classes should be tagged with a {@link PreRequisiteService} to identify dependencies.
 * 
 * @param <X> type service to be registered under
 * @author spb
 
 *
 */
public interface AppContextService<X extends AppContextService<X>> {

	/** {@link AppContext} is being closed.
	 * Only use this for cleanup that can't be handled by
	 * normal garbage collection or for state which is never returned by reference.
	 * 
	 */
	public void cleanup();
	
	/** Returns the type of service the class should be registered under.
	 * 
	 * @return registration type
	 */
	Class<? super X> getType();
}