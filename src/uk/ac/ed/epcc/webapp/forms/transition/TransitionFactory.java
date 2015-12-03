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
package uk.ac.ed.epcc.webapp.forms.transition;

import java.util.Set;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.forms.result.ChainedTransitionResult;
import uk.ac.ed.epcc.webapp.servlet.TransitionServlet;


/** A transition factory represents a set of bespoke operations (transitions) on a target type. 
 * 
 * Each transition is logically a
 * method on the target type. The parameters of the method (if any) are
 * represented by a form and the method returns an object (the return value of
 * the FormAction). It is also possible to define target-less transitions which are equivalent to 
 * static methods.
 * <p>
 * This interface does not require the target and key types to implement any particular interface.
 * All operations are methods on the transition factory. This is to allow greatest freedom of implementation.
 * <p>
 * The operations are identified using a key type. This is intended to be a lightweight type and 
 * may be as simple as a String or enum. Each key value must have a String representation as key values 
 * might be transferred as Strings. This String representation should be unique for the set of operations supported by any given target.
 * 
 * The separation into key and transition is not essential (they could be the same object or
 * tightly bound together). Operations that act on the 
 * set of all possible operations usually use the key type so instantiating all possible transition classes can be avoided 
 * it also makes it easier for the same named transition to invoke a different operation depending on the state of the target
 * or the roles of the operator. 
 * <p>
 * This interface is a way of allowing a class that supports many different operations to
 * describe the set of operations to the presentation logic.
 * <p> 
 * Most methods in this interface take a target parameter. Implementations are therefore free
 * to implement the associated logic in the TransitionProvider or in the target object
 * depending on whichever is most appropriate. The key type is another possible location 
 * for the access control logic.
 * <p>
 * In a web-context this interface needs to be extended to provide mappings between the target object and
 * string based identifiers that can be passed in form posts. e.g. {@link TransitionProvider}
 * @author spb
 * @param <K> key type
 * @param <T> target type
 * 
 */
public interface TransitionFactory<K,T> extends Contexed{
	/**
	 * Get a list of all the transition keys supported by the target type to be presented to the user
	 * as a set of possible options in a view transition. Options in this list
	 * that are not allowed by {@link #allowTransition(AppContext, Object, Object)} will not be presented.
	 * Additional hidden operations may also be supported but these will never be presented to the user
	 * as options and can only generated as a {@link ChainedTransitionResult}.
	 *  
	 * @param target
	 * @return Set of keys
	 */
	public Set<K> getTransitions(T target );
	/** Find a transition operator for this type by key.
	 * This method is permitted to return null for example if the current user
	 * is not permitted to perform the transition.
	 * 
	 * 
	 * @param target  target (may be null)
	 * @param key   key
	 * @return Transition or null
	 */
	public Transition<T> getTransition(T target,K key);
	/** Lookup a transition key by String
	 * This has to be the same as the result of the toString method
	 * on the key as this is what is passed from transitions.jsp for a form transition.
	 * @param target (may be null)
	 * @param name
	 * @return Transition key
	 */
	public K lookupTransition(T target, String name);
	
	/** What is the name for this type of transition.
	 * This value needs to resolve to the TransitionProvider
	 * in the {@link TransitionServlet}
	 * The value can also be used as the user presented text for the type of object
	 * in the title of the transition form page though this can be overridden by setting
	 * <b><i>target-name</i>.transition_title</b>
	 * @return String
	 */
	public String getTargetName();

	/** Access control check. Is this transition allowed for the current user/target
	 * @param c AppContext
	 * @param target target object
	 * @param key identifying key object for transition
	 * @return boolean is operation allowed
	 */
	public boolean allowTransition(AppContext c,T target,K key);
	/** Get target summary to be shown on transition page.
	 * In html this content is embedded within a div.block element.
	 * 
	 * @param c AppContext
	 * @param cb ContentBuilder
	 * @param target
	 * @return ContentBuilder
	 * 
	 */
	public <X extends ContentBuilder> X getSummaryContent(AppContext c,X cb,T target);


	/** Accept a {@link TransitionFactoryVisitor}.
	 * 
	 * Any code that depends on
	 * the particular sub-interface of {@link TransitionFactory} should implement {@link TransitionFactoryVisitor} this ensures it will always 
	 * handle all possible sub-classes.
	 * @param vis
	 * @return
	 */
	public <R> R accept(TransitionFactoryVisitor<R,T,K> vis);
}