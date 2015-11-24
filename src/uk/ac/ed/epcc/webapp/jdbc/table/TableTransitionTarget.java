// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.table;

import uk.ac.ed.epcc.webapp.Contexed;


/** Interface implemented by factory classes that support table transitions.
 * Most of the logic is delegated to TableTransitionRegistry in order to allow
 * the implementation to be by aggregation rather than by inheritance.
 * Inheritance could still be used by having the factory implement both interfaces
 * and return a self reference as the registry.
 * @author spb
 *
 */
public interface TableTransitionTarget extends Contexed {
	/** get TableTransitionRegisty
	 * 
	 * @return TableTransitionRegistry
	 */
    public TableTransitionRegistry getTableTransitionRegistry();
	/** Table name needed to look up this class in the Configuration Properties
	 * 
	 * @return String
	 */
	public String getTableTransitionID();

	
}