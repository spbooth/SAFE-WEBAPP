// Copyright - The University of Edinburgh 2013
package uk.ac.ed.epcc.webapp.forms.transition;

import uk.ac.ed.epcc.webapp.content.ContentBuilder;

/** Interface for a {@link TransitionFactory} that 
 * provides additional top/botton content for navication
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: NavigationProvider.java,v 1.2 2014/09/15 14:30:22 spb Exp $")
public interface NavigationProvider<K,T> extends TransitionFactory<K, T> {

	public <X extends ContentBuilder> X getTopNavigation(X cb, T target);

	
	public <X extends ContentBuilder> X getBottomNavigation(X cb, T target);



}
