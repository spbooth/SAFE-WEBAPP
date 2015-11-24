// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms;

import uk.ac.ed.epcc.webapp.forms.action.FormAction;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionValidationException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;

/**
 * Interface for objects that can perform a global consistency check on a Form
 * 
 * If it is more convenient to perform validation as part of the {@link FormAction}
 * then you can throw a {@link TransitionValidationException} there instead.
 * 
 * @author spb
 * 
 */
public interface FormValidator {
	public void validate(Form f) throws ValidateException;
}