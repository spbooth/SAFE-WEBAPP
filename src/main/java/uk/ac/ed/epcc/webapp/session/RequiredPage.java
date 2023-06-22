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

import java.util.Set;

import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.jdbc.filter.BaseFilter;
import uk.ac.ed.epcc.webapp.jdbc.filter.FalseFilter;
import uk.ac.ed.epcc.webapp.model.data.Composite;
import uk.ac.ed.epcc.webapp.servlet.ServletFormResultVisitor;

/** Object representing a page that a user is redirected to if certain conditions are met.
 * e.g. a password reset page.
 * 
 * You must ensure that any JSP referenced in this way does not include
 * the redirect checks itself to avoid looping. Some {@link FormResult}s are
 * added as request attributes by {@link ServletFormResultVisitor} and are checked to prevent loops.
 * 
 * {@link Composite}s in the {@link AppUserFactory} can add these if they implement {@link RequiredPageProvider}
 * 
 * @author spb
 * @param <U> type of {@link AppUser}
 *
 */
public interface RequiredPage<U extends AppUser> {
	
	public static final String AM_REQUIRED_PAGE_ATTR="AmRequiredPage";
	
	public static final String REQUIRED_PAGES_ATTR="RequiredPages";
	public static final String REQUIRED_PAGE_RETURN_ATTR="RequiredPageReturn";
   
	/** Does this required page need to be shown now
	 * 
	 * @param user
	 * @return
	 */
	public boolean required(SessionService<U> user);
	/** What page should be shown
	 * 
	 * @param user
	 * @return
	 */
    public FormResult getPage(SessionService<U> user);
    
    
   
    /** Filter for people who should be notified by email of a message
     * (e.g. update required).
     * 
     * @param sess
     * @return
     */
    default BaseFilter<U> notifiable(SessionService<U> sess){
    	return new FalseFilter<U>() ;
    }
    
    /** If a notification email is being sent (possibly triggered 
     * by a different required page). Is there any message we want to add.
     * 
     */
    default void addNotifyText(Set<String> notices,U person) {
    
    }
}