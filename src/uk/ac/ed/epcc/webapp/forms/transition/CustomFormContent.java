// Copyright - The University of Edinburgh 2011
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