// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data.forms.registry;

import uk.ac.ed.epcc.webapp.forms.registry.FormPolicy;
import uk.ac.ed.epcc.webapp.session.SessionService;
/** Form policy that applies to any registered user
 * 
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: UserPolicy.java,v 1.3 2014/09/15 14:30:32 spb Exp $")

public class UserPolicy implements FormPolicy {
    boolean create;
    boolean update;
    public UserPolicy(boolean create,boolean update){
    	this.create=create;
    	this.update=update;
    }
	public boolean canCreate(SessionService p) {
		return create;
	}

	public boolean canUpdate(SessionService p) {
		return update;
	}

}