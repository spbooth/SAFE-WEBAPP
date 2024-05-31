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
 *  however this interface adds support for reflecting this as html validation markup
 * Normally the {@link Input#convert(Object)} method should
 * be able to parse string values for setting min/max values.
 * 
 * @author spb
 * @param <T> type of input
 *
 */
public interface BoundedInput<T extends Comparable<T>> extends HTML5Input, MinMaxInput<T> {
	
	
	
	/** format step/range values as used by the input into compatible to the way they are  
	 * presented. for example a percent input may use 0.0 and 1.0 but present as 0, 100
	 * used  to generate HTML5 ranges.
	 * 
	 * @param n
	 * @return
	 */
	public abstract String formatRange(T n);
	
}
