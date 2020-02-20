//| Copyright - The University of Edinburgh 2020                            |
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
package uk.ac.ed.epcc.webapp.model.data;

import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.FormValidator;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.inputs.ListInput;
import uk.ac.ed.epcc.webapp.forms.inputs.UnmodifiableInput;
import uk.ac.ed.epcc.webapp.model.data.forms.Selector;

/** Interface for form constraints that restrict values of a field based on the values in
 * other fields. These are designed to be implemented as multi-stage forms so the constrained fields are
 * show after the dependencies.
 * 
 * @author Stephen Booth
 *
 */
public interface FieldConstraint {
	/** Mutate a {@link Selector} based on other form fields.
	 * Normally this requires the value of the input to have a fixed value
	 * e.g. an {@link UnmodifiableInput} or a {@link ListInput} with a single valid option.
	 * If the prerequisite fields are not fixed or not present then this method can return null
	 * to request an additional form stage be introduced. The form builder should then retry this
	 * field in subsequent stages (where previous fields will be locked).
	 * 
	 * Ideally if <b>support_multi_stage</b> is false the method should add a corresponding {@link FormValidator} to the form instead and return
	 * the original {@link Selector}
	 *
	 * 
	 * @param <I> type of {@link Input}
	 * @param support_multi_stage Does surrounding context support multi-stage
	 * @param field Field that Selector is for
	 * @param original {@link Selector}
	 * @param form {@link Form} being built.
	 * @return mutated {@link Selector} or null to request an additional form stage.
	 */
	public <I extends Input> Selector<I> apply(boolean support_multi_stage,String field, Selector<I> original, Form form);

	/** Merge two {@link FieldConstraint}s
	 *  values can be null
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static FieldConstraint add(FieldConstraint a, FieldConstraint b) {
		if( a == null ) {
			return b;
		}
		if( b == null ) {
			return a;
		}
		return new MultiFieldConstraint(a, b);
	}
}
