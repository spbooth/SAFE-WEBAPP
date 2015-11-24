// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.transition;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;

/** A transition that requires a form to specify arguments
 * This interface is not implemented directly but defines the signature
 * for both {@link FormTransition} and {@link ValidatingFormTransition}.
 * <p>
 * The appearance of the form can be customised by implementing {@link CustomFormContent}.
 * 
 * @author spb
 * @param <X> type of object transition is on
 *
 */
public  interface BaseFormTransition<X> extends Transition<X>{
	/**
	 * Build the transition form. The actions of this form should return a TransitionResult
	 * 
	 * @param f
	 *            Form to be built
	 * @param target
	 *            target Object
	 * @param conn
	 * @throws TransitionException
	 */
	public void buildForm(Form f, X target,AppContext conn) throws TransitionException;
}