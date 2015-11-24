// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.transition;

import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.result.FormResultVisitor;

/** Visitor to actually implement the transition logic.
 * 
 * All changes to model state should take place within the {@link TransitionVisitor}. This returns a 
 * {@link FormResult} that encodes the new view/display state that should be implemented by a {@link FormResultVisitor}.
 * 
 * The visitor pattern  is used to ensure an explicit dependency between the types of {@link Transition} and the code that
 * has to implement logic on them. Any additional {@link Transition} sub-types will either extend an existing type and use its implementation method
 * or require a change to the visitor interface.
 * 
 * @author spb
 *
 * @param <X>
 */
public interface TransitionVisitor<X> {
   FormResult doDirectTransition(DirectTransition<X> t) throws TransitionException;
   FormResult doDirectTargetlessTransition(DirectTargetlessTransition<X> t) throws TransitionException;
   FormResult doFormTransition(FormTransition<X> t) throws TransitionException;
   FormResult doValidatingFormTransition(ValidatingFormTransition<X> t) throws TransitionException;
   FormResult doTargetLessTransition(TargetLessTransition<X> t) throws TransitionException;
}