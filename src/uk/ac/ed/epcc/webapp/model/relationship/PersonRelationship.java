// Copyright - The University of Edinburgh 2014
package uk.ac.ed.epcc.webapp.model.relationship;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** A relationship between different users of the system
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: PersonRelationship.java,v 1.2 2014/09/15 14:30:34 spb Exp $")
public class PersonRelationship<A extends AppUser> extends Relationship<A, A>{

	/** role to allow sudo access
	 * 
	 */
	public static final String SUDO_ROLE = "Sudo";

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public PersonRelationship(AppContext c, String tag) {
		super(c,tag,c.getService(SessionService.class).getLoginFactory());
	}

	@Override
	protected String[] getDefaultRoles(AppContext c, String table) {
		return new String[] {SUDO_ROLE};
	}

}
