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