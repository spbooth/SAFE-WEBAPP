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