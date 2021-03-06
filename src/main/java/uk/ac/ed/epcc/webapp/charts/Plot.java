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

/** A class representing plot data
 * @author spb
 *
 */

public interface Plot {

	/** rescale all the data by a factor
	 * 
	 * @param scale
	 */
	public abstract void scale(float scale);
	
	/** add the data from another plot of the same type as this one.
	 * 
	 * @param p
	 */
	public void addData(Plot p);

}