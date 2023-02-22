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

import java.util.Iterator;

import uk.ac.ed.epcc.webapp.time.TimePeriod;

/** Each set of data consists of a sequence of time periods.
 * The time periods do not have to be of equal length. The same
 * set of time periods apply to each set of data.
 * @author spb
 *
 */
public interface PeriodSequencePlot extends PeriodPlot {

	/** re-scale all data values to by factor divided by the
	 * number of milliseconds in the period. This generates a rate value that will be correct
	 * even if the periods are not equal lengths.
	 * 
	 * @param scale
	 */
	public void rateScale(double scale);
	
	/** Convert the data-set to a cumulative graph.
	 * each value is replaced by the sum of previous values
	 * in the set plus the initial value for that set then scaled by the scale value.
	 * At the end of this operation the initial vector will have been incremented by the
	 * accumulated (un-scaled) values from each set in the original state.
	 * 
	 * @param scale
	 * @param initial
	 */
	public void scaleCumulative(double scale, double initial[]);
	
	/** copy the contents of one set to another scaling the values.
	 * 
	 * @param scale
	 * @param src
	 * @param dest
	 */
	public void scaleCopy(double scale, int src, int dest);
	
	/** get an {@link Iterator} over the time periods in the sequence.
	 * 
	 * @return
	 */
	public Iterator<TimePeriod> getSubPeriods();
	
	/** convert to stacked (each set stacks on top of each other).
	 * 
	 */
	public abstract void doConvertToStacked();
	/**
	 * rescale a dataset by constant divided by the value in a different dataset
	 * The normalisation dataset is assumed to have a single Set belong to the same class 
	 * as this object and use the same set of time periods.
	 
	 * @param scale
	 * @param norm
	 */
	public void datasetScale(double scale, PeriodSequencePlot norm);
	
	
}