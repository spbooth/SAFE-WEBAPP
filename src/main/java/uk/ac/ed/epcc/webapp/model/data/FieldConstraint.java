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
import java.util.Map;

import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.FormValidator;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.inputs.ListInput;
import uk.ac.ed.epcc.webapp.forms.inputs.UnmodifiableInput;
import uk.ac.ed.epcc.webapp.model.data.forms.Selector;
import uk.ac.ed.epcc.webapp.validation.FieldValidationSet;

/** Interface for form constraints that restrict values of a field based on the values in
 * other fields. These are designed to be implemented as multi-stage forms so the constrained fields are
 * show after the dependencies. The fixtures are the inputs to the constraints and are the fixed form fields from previous stages,
 * fields with known values (e.g.an {@link UnmodifiableInput} or a {@link ListInput} with a single valid option)
 * and possibly object fields not show in the form.
 * 
 * {@link FieldConstraint}s should be backed up by a {@link FormValidator} to cover any {@link Form} types that don't support
 * multi-stage forms.
 * 
 * @author Stephen Booth
 * @param <D> type of field/input
 *  
 */
public interface FieldConstraint<D> {
	/** Should this form field be skipped entirely based on other form fields. 
	 * e.g a text box that is only used for an "other" response
	 
	 * @param fixtures
	 * @return
	 */
	default public  boolean suppress(Map<String,Object> fixtures) {
		return false;
	}
	/** request this input be deferred to a later display stage because some of the required fields are nor present in the fixtures
	 *  Care needs to be taken with pre-requisites that are optional.
	 * @param fixtures
	 * @return
	 */
	default boolean requestMultiStage(Map<String,Object> fixtures) {
		return false;
	}
	
	/** Mutate a {@link Selector} based on fixture values.
	 * Note that in many cases it may be better to mutate the {@link FieldValidationSet}
	 *
	 * @param <I> type of {@link Input}
	 * @param original {@link Selector}
	 * @param fixtures {@link HashMap} of fixed field data. This can include supressed fields. note values of optional fields may be null.
	 * @return mutated {@link Selector} or null to request an additional form stage.
	 */
	public default <I extends Input<D>> Selector<I> changeSelector(Selector<I> original, Map<String,Object> fixtures){
		return original;
	}

	/** Change the optional setting for the field.
	 * 
	 * 
	 * @param original
	 * @param fixtures
	 * @return
	 */
	public default boolean changeOptional(boolean original, Map<String,Object> fixtures) {
		return original;
	}
	
	/** Mutate a field default/initial value based on  other form fields. 
	 * Note for update forms the original value is the current value of the form so
	 * this should only be changed if an earlier choice invalidates that value and
	 * there is a sensible new default that could be suggested as a replacement.
	 * 
	
	 * @param original
	 * @param fixtures
	 * @return
	 */
	default public  D defaultValue( D original, Map<String,Object> fixtures) {
		return original;
	}
	/** Mutate the 
	 * 
	 * @param original
	 * @param fixtures
	 * @return
	 */
	default public FieldValidationSet<D> validationSet(FieldValidationSet<D> original, Map<String,Object> fixtures){
		return original;
	}
	/** Get a {@link FormValidator} corresponding to this constraint.
	 * This is to be used to implement the constraint when mult-stage forms are not supported
	 * 
	 * @return {@link FormValidator} or null;
	 */
	default public FormValidator getFormValidator() {
		return null;
	}
	/** Merge two {@link FieldConstraint}s
	 *  values can be null
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static <D> FieldConstraint<D> add(FieldConstraint<D> a, FieldConstraint<D> b) {
		if( a == null ) {
			return b;
		}
		if( b == null ) {
			return a;
		}
		return new MultiFieldConstraint<D>(a, b);
	}
}
