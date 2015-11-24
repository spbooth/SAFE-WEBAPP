// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms;

import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;

/**
 * Interface for an additional external validator that can be added to a Field
 * 
 * @author spb
 * @param <D> Type of input data
 */
public interface FieldValidator<D> {

	public  void validate(D data)
			throws ValidateException;
}