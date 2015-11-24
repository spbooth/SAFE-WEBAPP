// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.transition;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;

/** An argument-less transition that can proceed directly
 * 
 * @author spb
 * @param <X> Type of object transition is on
 */
public interface DirectTransition<X> extends Transition<X>{
	public FormResult doTransition(X target,AppContext c) throws TransitionException;
}