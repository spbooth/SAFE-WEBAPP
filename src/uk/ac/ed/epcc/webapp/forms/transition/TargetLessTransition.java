// Copyright - The University of Edinburgh 2011
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