// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.inputs;

public interface OptionalInput {
	/**
	 * Is it an error not to specify this Input
	 * 
	 * @return boolean true if optional
	 * 
	 */
	public boolean isOptional();

	/**
	 * Mark this input as optional or not.
	 * This may do nothing if the Input should never be optional.
	 * 
	 * @param opt
	 *            boolean
	 */
	public void setOptional(boolean opt);
}