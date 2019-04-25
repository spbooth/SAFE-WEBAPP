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