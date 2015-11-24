// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.session;

import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.model.data.Composite;

/** Object representing a page that a user is redirected to if certain conditions are met.
 * e.g. a password reset page.
 * 
 * {@link Composite}s in the {@link AppUserFactory} can add these if they implement {@link RequiredPageProvider}
 * 
 * @author spb
 * @param <U> type of {@link AppUser}
 *
 */
public interface RequiredPage<U extends AppUser> {
    public boolean required(SessionService<U> user);
    public FormResult getPage();
}