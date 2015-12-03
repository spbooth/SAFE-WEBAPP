//| Copyright - The University of Edinburgh 2011                            |
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