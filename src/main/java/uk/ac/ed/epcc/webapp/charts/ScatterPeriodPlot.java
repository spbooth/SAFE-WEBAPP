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

import uk.ac.ed.epcc.webapp.charts.strategy.RangeMapper;

/**
 * @author spb
 *
 */

public interface ScatterPeriodPlot  extends Plot{
	/**
	 * Basic method for mapping an object into the plot. 
	 * <p>
	 * Note that the same interface can
	 * be used to map individual records or to query a factory for a required period.
	 * 
	 * @param x A {@link RangeMapper} to generate the X coordinate
	 * @param y A {@link RangeMapper} to generate the Y coordinate
	 * @param object
	 * @throws uk.ac.ed.epcc.webapp.charts.InvalidTransformException
	 */
	public <D> void addData(RangeMapper<D> x, RangeMapper<D> y, D object)
			throws uk.ac.ed.epcc.webapp.charts.InvalidTransformException;

	public void addPoint(float x, float y);
	public void setLabel(String key);
}