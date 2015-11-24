// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.jdbc.table;


/** A TableTransitionTarget where some transitions may edit the table structure.
 * We therefore need a call-back to allow the target to regenerate any internal state that depends
 * on the table structure
 * 
 * 
 * @author spb
 *
 */
public interface TableStructureTransitionTarget extends TableTransitionTarget {
	/** Table structure has changed
	 * 
	 */
	public void resetStructure();
}