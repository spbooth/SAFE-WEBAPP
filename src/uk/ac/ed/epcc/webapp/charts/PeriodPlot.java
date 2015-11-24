// Copyright - The University of Edinburgh 2014
package uk.ac.ed.epcc.webapp.charts;

import uk.ac.ed.epcc.webapp.charts.strategy.QueryMapper;
import uk.ac.ed.epcc.webapp.charts.strategy.SetRangeMapper;

/** A {@link SetPlot} where each data value in a set corresponds to a period of time.
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: PeriodPlot.java,v 1.3 2014/09/15 14:30:12 spb Exp $")
public interface PeriodPlot extends SetPlot {
	/**
	 * Basic method for mapping an object into the plot
	 * 
	 * @param t
	 * @param object
	 * @throws uk.ac.ed.epcc.webapp.charts.InvalidTransformException
	 */
	public <D> void addData(SetRangeMapper<D> t, D object)
			throws uk.ac.ed.epcc.webapp.charts.InvalidTransformException;

	/** Populate plot using a QueryMapper and a factory 
	 * 
	 * @param <F> type of factory
	 * @param t  QueryMapper to use
	 * @param fac Factory to query
	 * @return true if data added
	 * @throws uk.ac.ed.epcc.webapp.charts.InvalidTransformException 
	 */
	public abstract <F> boolean addMapData(QueryMapper<F> t, F fac)
	        throws uk.ac.ed.epcc.webapp.charts.InvalidTransformException;
}
