//| Copyright - The University of Edinburgh 2014                            |
//|                                                                         |
//| Licensed under the Apache License, Version 2.0 (the "License");         |
//| you may not use this file except in compliance with the License.        |
//| You may obtain a copy of the License at                                 |
//|                                                                         |
//|    http://www.apache.org/licenses/LICENSE-2.0                           |
//|                                                                         |
//| Unless required by applicable law or agreed to in writing, software     |
//| distributed under the License is distributed on an "AS IS" BASIS,       |
//| WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.|
//| See the License for the specific language governing permissions and     |
//| limitations under the License.                                          |
package uk.ac.ed.epcc.webapp.charts;

import uk.ac.ed.epcc.webapp.charts.strategy.QueryMapper;
import uk.ac.ed.epcc.webapp.charts.strategy.SetRangeMapper;

/** A {@link SetPlot} where each data value in a set corresponds to a period of time.
 * @author spb
 *
 */
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