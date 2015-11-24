// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.result;

import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory;


/** A {@link FormResult} to recurse to a new transition after this one.
 * 
 * Note that we can pass information to the next transition by caching it in the 
 *  {@link uk.ac.ed.epcc.webapp.session.SessionService}
 * to construct a sequence of forms. Care needs to be taken to remove cached 
 * values to prevent unintended side effects.
 * 
 * 
 * @author spb
 * @param <T> Target type
 * @param <K> Key type
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: ChainedTransitionResult.java,v 1.4 2014/09/15 14:30:21 spb Exp $")

public class ChainedTransitionResult<T,K> implements FormResult {
 private final K key;
 private final T target;
 private final TransitionFactory<K, T> provider;
 
 public ChainedTransitionResult(TransitionFactory<K,T> provider,T target,K next){
   this.provider=provider;
   this.key=next;
   this.target=target;
 }
 public K getTransition(){
	 return key;
 }
 public TransitionFactory<K,T> getProvider(){
	 return provider;
 }
 public T getTarget(){
	 return target;
 }
public void accept(FormResultVisitor vis) throws Exception {
	// view transition sub-classes should useURL
	//assert( target != null || useURL());

	vis.visitChainedTransitionResult(this);
}
/** Is this a transition to a bookmarkable location.
 * In web implementations if this method returns true then we redirect 
 * to the URL instead of just forwarding to logic. This means the page is 
 * at a URL that can be bookmarked.
 * It is also useful for results from state changing operations as this prevents the
 * back button from re-submitting the operation.
 * 
 * @return true if URL needed
 */
public boolean useURL(){
	return false;
}
}