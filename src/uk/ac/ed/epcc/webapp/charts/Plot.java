// Copyright - The University of Edinburgh 2014
package uk.ac.ed.epcc.webapp.charts;

/**
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: Plot.java,v 1.12 2014/09/15 14:30:12 spb Exp $")
public interface Plot {

	/** rescale all the data by a factor
	 * 
	 * @param scale
	 */
	public abstract void scale(float scale);

}