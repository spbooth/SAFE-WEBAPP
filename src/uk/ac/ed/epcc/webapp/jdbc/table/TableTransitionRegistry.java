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

import java.util.Set;

import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.forms.transition.Transition;
import uk.ac.ed.epcc.webapp.session.SessionService;

public interface TableTransitionRegistry {

	/** get content to be shown in transition form.
	 * @param hb 
	 * 
	 * @param operator
	 */
	public void getTableTransitionSummary(ContentBuilder hb,SessionService operator);

	/** lookup Transition using key
	 * 
	 * @param name TransitionKey
	 * @return Transition
	 */
	public Transition<TableTransitionTarget> getTableTransition(TableTransitionKey name);

	/** What operations are supported.
	 * 
	 * @return Set of TransitionKey
	 */
	public Set<TableTransitionKey> getTableTransitionKeys();

}