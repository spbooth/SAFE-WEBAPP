// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.registry;

import uk.ac.ed.epcc.webapp.session.SessionService;

public interface FormPolicy {
	/** Can the current user update
	 * 
	 * @param p Person
	 * @return boolean
	 */
	boolean canUpdate(SessionService p);

	/** Can the current user create
	 * 
	 * @param p Person
	 * @return boolean
	 */
	boolean canCreate(SessionService p);

}