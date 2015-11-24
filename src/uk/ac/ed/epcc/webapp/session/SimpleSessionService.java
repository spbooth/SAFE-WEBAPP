// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.session;

import uk.ac.ed.epcc.webapp.AppContext;

/** SessionService where the session information is just stored in the AppContext
 * therefore only suitable for command line apps where there is a single AppContext
 * 
 * @author spb
 *
 * @param <A>
 */
@uk.ac.ed.epcc.webapp.Version("$Id: SimpleSessionService.java,v 1.4 2014/09/15 14:30:36 spb Exp $")

public class SimpleSessionService<A extends AppUser> extends AbstractSessionService<A> {

	public SimpleSessionService(AppContext c) {
		super(c);
	}

	public final void setAttribute(String key, Object value) {
		c.setAttribute(key, value);
	}

	public final void removeAttribute(String key) {
		c.removeAttribute(key);
		
	}

	public final Object getAttribute(String key) {
		return c.getAttribute(key);
	}

	
}