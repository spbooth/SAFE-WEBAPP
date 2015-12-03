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

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;

/** A transition that generates a Form to supply all input.
 * There is no implied target.
 * This is needed for creation forms and forms that select the target for
 * a nested transition or that create new targets.
 * <p>
 * The appearance of the form can be customised by implementing {@link CustomFormContent}.
 * @author spb
 *
 * @param <X> target type of transition
 */
public interface TargetLessTransition<X> extends Transition<X> {
	/**
	 * Build the transition form. The actions of this form should return a TransitionResult
	 * 
	 * @param f
	 *            Form to be built
	 * @param c AppContext
	 * @throws TransitionException
	 */
	public void buildForm(Form f, AppContext c) throws TransitionException;
}