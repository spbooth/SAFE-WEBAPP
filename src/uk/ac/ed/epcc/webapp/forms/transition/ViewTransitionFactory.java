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

import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** A {@link TransitionFactory} that can use the generic view_target pages.
 * These show a html summary of the object and give buttons to invoke the possible
 * transitions on the target. The view page is shown if no specific transition is requested.
 * This is incompatible with {@link DefaultingTransitionFactory} if both interfaces are implemented the
 * view operation will take precedence.
 * 
 * @author spb
 *
 * @param <K> key type
 * @param <T> target type
 * @see ViewTransitionProvider
 * @see ViewPathTransitionProvider
 */
public interface ViewTransitionFactory<K, T> extends TransitionFactory<K, T> {
	/** Can the current person view this target
	 * @param target
	 * @param sess
	 * @return boolean
	 */
   public boolean canView(T target, SessionService<?> sess);
   /** Get the content to be displayed at the top of the target page.
    * This content is at the top level of the page
    * 
    * @param cb
    * @param target
    * @param sess
    * @return boolean
    */
   public <X extends ContentBuilder> X getTopContent(X cb,T target, SessionService<?> sess);

   /** Get the content to be displayed at the bottom of the target page.
    * This content is at the top level of the page
    * 
    * @param cb
    * @param target
    * @param sess
    * @return boolean
    */
   public <X extends ContentBuilder> X getBottomContent(X cb,T target, SessionService<?> sess);

   /** Get the content to be displayed on the view target page as part of the target pane.
    * 
    * @param cb
    * @param target
    * @param sess
    * @return ContentBuilder
    */
   public <X extends ContentBuilder> X getLogContent(X cb,T target, SessionService<?> sess);
   /** Get tooltip help string for an operation
    * 
    * @param key
    * @return String or null
    */
   public String getHelp(K key);
   
   /** get custom button content. should default to the string representation of the key
    * 
    * @param key
    * @return String
    */
   public String getText(K key);
}