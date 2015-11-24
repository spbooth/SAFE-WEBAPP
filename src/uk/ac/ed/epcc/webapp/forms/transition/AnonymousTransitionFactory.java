// Copyright - The University of Edinburgh 2013
package uk.ac.ed.epcc.webapp.forms.transition;

/** A {@link TransitionFactory} that is allowed to implement anonymous transitions.
 * That is where there is no {@link SessionService} or no current user.
 * 
 * 
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: AnonymousTransitionFactory.java,v 1.3 2014/09/15 14:30:22 spb Exp $")
public interface AnonymousTransitionFactory<K,T> extends TransitionFactory<K, T> {

}
