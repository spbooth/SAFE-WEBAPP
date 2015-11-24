// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.model.data;

/**
 * Interface for DataObjects that support a Retire action
 * 
 * @author spb
 * 
 */
public interface Retirable {
	/**
	 * Is this object in a state that allows it to be retired.
	 * 
	 * @return boolean true if retire is possible
	 */
	public boolean canRetire();

	/**
	 * retire this object.
	 * 
	 * @throws Exception
	 */
	public void retire() throws Exception;
}