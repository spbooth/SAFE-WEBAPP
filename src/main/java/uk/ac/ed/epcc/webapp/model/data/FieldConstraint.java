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

import java.util.HashMap;

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
	 * e.g. an {@link UnmodifiableInput}, a {@link ListInput} with a single valid option or a suppressed field.
	 * If the prerequisite fields are not fixed or not present then this method can return null
	 * to request an additional form stage be introduced. The form builder should then retry this
	 * field in subsequent stages (where previous fields will be locked). Fixed values are copied into the
	 * <b>fixtures</b> map. This also can contain values for supressed fields. Care needs to be taken with pre-requisites that
	 * are optional.
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
	 * @param fixtures {@link HashMap} of fixed field data. This can include supressed fields. note values of optional fields may be null.
	 * @return mutated {@link Selector} or null to request an additional form stage.
	 */
	public <I extends Input> Selector<I> apply(boolean support_multi_stage,String field, Selector<I> original, Form form,HashMap fixtures);

	public default boolean changeOptional(String field, boolean original, Form form,HashMap fixtures) {
		return original;
	}
	/** Should this form field be skipped entirely based on other form fields. 
	 * e.g a text box that is only used for an "other" response
	 * @param <I>
	
	 * @param field
	 * @param original
	 * @param form
	 * @param fixtures
	 * @return
	 */
	default public <I extends Input>  boolean suppress(String field, Selector<I> original, Form form,HashMap fixtures) {
		return false;
	}
	/** Mutate a field default/initial value based on  other form fields. 
	 * Note for update forms the original value is the current value of the form so
	 * this should only be changed if an earlier choice invalidates that value and
	 * there is a sensible new default that could be suggested as a replacement.
	 * 
	 * @param <D>
	 * @param <I>
	 * @param field
	 * @param original
	 * @param form
	 * @param fixtures
	 * @return
	 */
	default public <D,I extends Input<D>> D defaultValue(String field, D original, Form form, HashMap fixtures) {
		return original;
	}
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
