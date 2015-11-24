// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.transition;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;

/** An argument-less transition with a fixed operation.
 * i.e. one that does not require a target or a parameter form.
 * 
 * @author spb
 * @param <X> Type of object transition is on
 */
public interface DirectTargetlessTransition<X> extends Transition<X>{
	public FormResult doTransition(AppContext c) throws TransitionException;
}