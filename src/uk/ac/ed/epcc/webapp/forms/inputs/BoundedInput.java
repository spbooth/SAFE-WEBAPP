//| Copyright - The University of Edinburgh 2016                            |
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
package uk.ac.ed.epcc.webapp.forms.inputs;

/**
 * @author spb
 *
 */
public interface BoundedInput<T> extends HTML5Input {
	/** Minimum valid  value.
	 * null value implies no minimum.
	 * 
	 * @return T
	 */
	public abstract T getMin();

	/** Maximum  valid value
	 * null value implies no maximum
	 * 
	 * @return T
	 */
	public abstract T getMax();
	
	/** format step/range values compatible to the way they are  
	 * presented. for example a percent imput may use 0.0 and 1.0 but present as 0, 100
	 * used  to generate HTML5 ranges.
	 * 
	 * @param n
	 * @return
	 */
	public abstract String formatRange(T n);
	/** Set the minimum value
	 * 
	 * @param val
	 * @return previous limit
	 */
	public abstract T setMin(T val);
	/** set the maximum value
	 * 
	 * @param val
	 * @return previous limit
	 */
	public abstract T setMax(T val);
}
