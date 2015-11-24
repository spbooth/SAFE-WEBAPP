// Copyright - The University of Edinburgh 2011
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
@uk.ac.ed.epcc.webapp.Version("$Id: TransitionException.java,v 1.5 2015/09/22 14:10:15 spb Exp $")

public class TransitionException extends ActionException {
	
	private static final long serialVersionUID = 1L;

	public TransitionException(String message) {
		super(message);
	}
}