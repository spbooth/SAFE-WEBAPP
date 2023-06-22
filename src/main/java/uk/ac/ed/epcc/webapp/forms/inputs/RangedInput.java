//| Copyright - The University of Edinburgh 2013                            |
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

/** An Input where the values must come from a numerical range.
 * 
 * This can be used to add HTML5 validation 
 * @author spb
 *
 * @param <N> type of input
 */

public interface RangedInput<N extends Number> extends BoundedInput<N>{

	
	/** Step value
	 * defines step value. Valid values should be a multiple of the setp value.
	 * null value implies unconstrained.
	 * This is used to drive the html number input.
	 * 
	 * @return
	 */
	public abstract Number getStep();
	
	

}