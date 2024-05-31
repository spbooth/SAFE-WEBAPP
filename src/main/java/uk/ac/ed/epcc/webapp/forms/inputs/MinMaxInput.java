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

import java.util.Set;

import uk.ac.ed.epcc.webapp.validation.FieldValidator;
import uk.ac.ed.epcc.webapp.validation.MaxValueValidator;
import uk.ac.ed.epcc.webapp.validation.MinValueValidator;

/** Interface for {@link Input} which come from a range.
 *  The actual range limits should be set by adding {@link FieldValidator}s
 * 
 * @author spb
 * @param <T> type of input
 *
 */
public interface MinMaxInput<T extends Comparable<T>> extends Input<T> {
	/** Minimum valid  value.
	 * null value implies no minimum.
	 * 
	 * @return T
	 */
	default public  T getMin() {
		return (T) MinValueValidator.getMin((Set) getValidators());
	}

	/** Maximum  valid value
	 * null value implies no maximum
	 * 
	 * @return T
	 */
	default public  T getMax() {
		return (T) MaxValueValidator.getMax((Set) getValidators());
	}
	

	/** Set the minimum value
	 * 
	 * @param val
	 */
	default public void setMin(T val) {
		if( val == null) {
			return;
		}
		addValidator(new MinValueValidator<T>(val));
	}
	/** set the maximum value
	 * 
	 * @param val
	 */
	default public void setMax(T val) {
		if( val == null) {
			return;
		}
		addValidator(new MaxValueValidator<T>(val));
	}
}
