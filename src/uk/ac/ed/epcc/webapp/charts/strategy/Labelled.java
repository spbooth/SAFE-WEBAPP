// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.charts.strategy;

import java.util.Vector;

/** Common interface for transformations that generate Labels for the categories they map to.
 * 
 * @author spb
 *
 */
public interface Labelled{
	/**
	 * What are the labels corresponding to the Sets
	 * 
	 * @return Vector of Strings
	 */
	public Vector<String> getLabels();

	/**
	 * How many sets can the mapper map to
	 * 
	 * @return int number of sets
	 */
	public int nSets();
}