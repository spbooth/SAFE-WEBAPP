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
package uk.ac.ed.epcc.webapp.forms.result;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactoryFinder;
import uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactory;
import uk.ac.ed.epcc.webapp.session.SessionService;


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


public class ChainedTransitionResult<T,K> implements FormResult {
 private final K key;
 private final T target;
 private final TransitionFactory<K, T> provider;
 
 public ChainedTransitionResult(TransitionFactory<K,T> provider,T target,K next){
   this.provider=provider;
   this.key=next;
   this.target=target;
 }
 
 /**Alternate constructor that performs a lookup of the {@link TransitionFactory}
	 * 
	 * This is preferable if an instance is not already available as it can utilise
	 * any cached copy.
	 * 
  * 
  * @param conn
  * @param template
  * @param tag
  * @param target
  * @param next
  */
 public ChainedTransitionResult(AppContext conn, Class<? extends TransitionFactory> template, String tag, T target, K next) {
	 this(TransitionFactoryFinder.getTransitionFactory(conn, template, tag),target,next);
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
 public boolean allow(SessionService<?> sess) {
	TransitionFactory<K, T> p = getProvider();
	K k = getTransition();
	if( k == null && p instanceof ViewTransitionFactory) {
		return ((ViewTransitionFactory<K, T>)p).canView(getTarget(), sess);
	}
	return p.allowTransition(sess.getContext(), getTarget(), k);
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
@Override
public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((key == null) ? 0 : key.hashCode());
	result = prime * result + ((provider == null) ? 0 : provider.hashCode());
	result = prime * result + ((target == null) ? 0 : target.hashCode());
	return result;
}
@Override
public boolean equals(Object obj) {
	if (this == obj)
		return true;
	if (obj == null)
		return false;
	if (getClass() != obj.getClass())
		return false;
	ChainedTransitionResult other = (ChainedTransitionResult) obj;
	if (key == null) {
		if (other.key != null)
			return false;
	} else if (!key.equals(other.key))
		return false;
	if (provider == null) {
		if (other.provider != null)
			return false;
	} else if (!provider.equals(other.provider))
		return false;
	if (target == null) {
		if (other.target != null)
			return false;
	} else if (!target.equals(other.target))
		return false;
	return true;
}


}