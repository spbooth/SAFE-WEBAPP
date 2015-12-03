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



/** A Transition Provider that can use the generic view_target pages.
 * These show a html summary of the object and give buttons to invoke the possible
 * transitions on the target.
 * 
 * @author spb
 *
 * @param <K> key type
 * @param <T> target type
 */
public interface ViewTransitionProvider<K, T> extends ViewTransitionFactory<K, T>,TransitionProvider<K, T> {
	
}