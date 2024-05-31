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
 * @param <X> type of transition target
 */
public interface TransitionVisitor<X> {
   FormResult doDirectTransition(DirectTransition<X> t) throws TransitionException;
   FormResult doDirectTargetlessTransition(DirectTargetlessTransition<X> t) throws TransitionException;
   FormResult doFormTransition(FormTransition<X> t) throws TransitionException;
   FormResult doValidatingFormTransition(ValidatingFormTransition<X> t) throws TransitionException;
   FormResult doTargetLessTransition(TargetLessTransition<X> t) throws TransitionException;
   FormResult doModalTransition(ModalTransition<X> t)throws TransitionException;
}