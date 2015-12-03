//| Copyright - The University of Edinburgh 2011                            |
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
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.charts.strategy;

import java.util.Date;
import java.util.Map;

/** Formatting object that gather the data for an arbitrary date range from 
 * a target object. Where RangeMapper is applied to individual data items
 * QueryMapper is applied to factory classes that can perform more high level
 * queries.
 * 
 * @author spb
 * @param <T> Type of class being mapped
 */
public interface QueryMapper<T> extends PlotStrategy{
	/** Get all the overlap for a data range
	 * 
	 * @param o target factory to query
	 * @param start Date start of period
	 * @param end Date end of period
	 * @return Map of overlaps keyed by set
	 */
	public Map<Integer,Number> getOverlapMap(T o,Date start,Date end);
}