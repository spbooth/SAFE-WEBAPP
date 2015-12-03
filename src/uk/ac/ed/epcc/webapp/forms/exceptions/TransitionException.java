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
package uk.ac.ed.epcc.webapp.forms.exceptions;


/**
 * Exception thrown if requested transition is invalid for this
 * target/operator The message string should be presented as an error
 * message to the operator. This should only be used when there are 
 * no data-base modifications to be rolled-back. If roll-back is required use the 
 * sub-class {@link FatalTransitionException}.
 * 
 * If you want the message shown as a form-error (so the form can be re-submitted)
 * then use a {@link TransitionValidationException}.
 * 
 * @see TransitionValidationException
 * @author spb
 * 
 */


public class TransitionException extends ActionException {
	
	private static final long serialVersionUID = 1L;

	public TransitionException(String message) {
		super(message);
	}
}