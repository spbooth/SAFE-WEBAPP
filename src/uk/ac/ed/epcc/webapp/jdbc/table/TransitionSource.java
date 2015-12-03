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
package uk.ac.ed.epcc.webapp.jdbc.table;

import java.util.Map;

import uk.ac.ed.epcc.webapp.forms.transition.Transition;
import uk.ac.ed.epcc.webapp.model.data.transition.TransitionKey;
/** Interface for objects that can augment the table {@link Transition}s of a
 * {@link TableTransitionTarget}.
 * 
 * @author spb
 *
 * @param <T>
 */
public interface TransitionSource<T extends TableTransitionTarget> {
	/** Generate a {@link Map} of {@link Transition}s to be added to the
	 * table transitions of the {@link TableTransitionTarget}. 
	 * 
	 * @return
	 */
	public Map<TransitionKey<T>,Transition<T>> getTransitions();

}