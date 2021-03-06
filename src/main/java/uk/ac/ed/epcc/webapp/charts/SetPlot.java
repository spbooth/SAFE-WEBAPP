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
package uk.ac.ed.epcc.webapp.charts;

import uk.ac.ed.epcc.webapp.content.Table;


/**
 * Plot represents the Data in the chart being created.
 * 
 * The data is grouped into sets with each set being displayed separately. 
 * @author spb
 * 
 */
public interface SetPlot extends Plot {

	
	/** set Number of sets in this {@link SetPlot}
	 * 
	 * @param nsets
	 */
	public abstract void setNumSets(int nsets);

	

	/**
	 * sum the contents of the dataset returning an array by set
	 * 
	 * @return double[]
	 */
	public abstract double[] getCounts();

	/** get the number of sets.
	 * 
	 * @return
	 */
	public abstract int getNumSets();

	/**
	 * permute the contents of the sets in a Dataset
	 * labels are also permuted/truncated.
	 * 
	 * @param new_nset
	 *            number of sets to truncate to
	 * @param perm
	 *            permutation array, can be many to one to merge sets
	 */
	public abstract void permSets(int new_nset, int[] perm);

	public boolean hasLegends();
	/**
	 * get the legend strings
	 * 
	 * @return String[] legends
	 */
	public String[] getLegends();
	/**
	 * set the legends strings These are the labels for the different sets in this {@link SetPlot}
	 * 
	 * @param leg
	 */
	public void setLegends(String leg[]);
	
	/** Create a table representing the data in this {@link SetPlot}.
	 * Each set of data produces a row of the table keyed by the corresponding label.
	 * The data columns produced are chosen by the implementing class subject to the advice that
	 * multiple {@link SetPlot}s that can co-exist in a single chart should also make sense when combined into  
	 * a single table.
	 * 
	 * @param quantity Quantity name if known
	 * @return Table
	 * 
	 */
	public Table getTable(String quantity);
}