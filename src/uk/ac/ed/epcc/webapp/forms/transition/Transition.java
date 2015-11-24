// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/

package uk.ac.ed.epcc.webapp.forms.transition;

import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;

/** Transition is like a virtual method call 
 * methods are named by the key type, operate on the target type and
 * return a result type.
 * 
 * As there are multiple types of transition that operate in different ways
 * we use a visitor pattern as this forces the dependency to be explicit.
 * @author spb
 *
 * @param <X> Target type for transition
 * 
 */
public interface Transition<X>{
    FormResult getResult(TransitionVisitor<X> vis) throws TransitionException;
}