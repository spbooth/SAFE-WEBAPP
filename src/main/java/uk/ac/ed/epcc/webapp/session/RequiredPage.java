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
package uk.ac.ed.epcc.webapp.session;

import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.model.data.Composite;
import uk.ac.ed.epcc.webapp.servlet.ServletFormResultVisitor;

/** Object representing a page that a user is redirected to if certain conditions are met.
 * e.g. a password reset page.
 * 
 * You must ensure that any JSP referenced in this way does not include
 * the redirect checks itself to avoid looping. Some {@link FormResult}s are
 * added as request attributes by {@link ServletFormResultVisitor} andare checked to prevent loops.
 * 
 * {@link Composite}s in the {@link AppUserFactory} can add these if they implement {@link RequiredPageProvider}
 * 
 * @author spb
 * @param <U> type of {@link AppUser}
 *
 */
public interface RequiredPage<U extends AppUser> {
    public boolean required(SessionService<U> user);
    public FormResult getPage(SessionService<U> user);
}