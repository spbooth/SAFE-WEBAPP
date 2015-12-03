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
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** Interface for {@link BaseFormTransition} or {@link TargetLessTransition}s 
 * that customise the presentation of the form itself.
 * <p>
 * This only modifies presentation. The form should still be functional if the default layout is used.
 * @author spb
 * @param <O> target object of transition
 *
 */

public interface CustomFormContent<O> {
	/** Add the form elements to the {@link ContentBuilder}. This should include all inputs including the action buttons.
	 * 
	 * The {@link Form} should already be fully built before calling this method.
	 * @param cb  ContentBuilder
	 * @param op  operator
	 * @param f	  Form
	 * @param target
	 * @return modified ContentBuilder
	 */
	public <X extends ContentBuilder> X addFormContent(X cb,SessionService<?> op, Form f,O target);
}