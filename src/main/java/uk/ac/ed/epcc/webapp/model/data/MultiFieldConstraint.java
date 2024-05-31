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
import java.util.LinkedHashSet;
import java.util.Map;

import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.model.data.forms.Selector;
import uk.ac.ed.epcc.webapp.validation.FieldValidationSet;



/** A composite {@link FieldConstraint}. The constaints are added in order
 * @author Stephen Booth
 *
 */
public class MultiFieldConstraint<D> extends LinkedHashSet<FieldConstraint<D>> implements FieldConstraint<D>{
	
	/**
	 * 
	 */
	public MultiFieldConstraint(FieldConstraint<D> ...constraints) {
		super();
		for(FieldConstraint<D> c : constraints) {
			add(c);
		}
	}

	@Override
	public boolean add(FieldConstraint<D> e) {
		if( e == null) {
			return false;
		}
		if( e.equals(this)) {
			return false;
		}
		if( e.getClass() == getClass()) {
			return addAll((MultiFieldConstraint)e);
		}
		return super.add(e);
	}

	

	@Override
	public boolean suppress(Map<String,Object> fixtures) {
		for(FieldConstraint<D> c: this) {
			if( c.suppress(  fixtures)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean changeOptional(boolean original, Map<String,Object> fixtures) {
		for(FieldConstraint<D> c : this) {
			original = c.changeOptional(original,fixtures);
		}
		return original;
	}

	@Override
	public boolean requestMultiStage(Map<String, Object> fixtures) {
		for(FieldConstraint<D> c : this) {
			if( c.requestMultiStage(fixtures)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public <I extends Input<D>> Selector<I> changeSelector(Selector<I> original, Map<String, Object> fixtures) {
		for(FieldConstraint<D> c : this) {
			original = c.changeSelector(original, fixtures);
		}
		return original;
	}

	@Override
	public D defaultValue(D original, Map<String, Object> fixtures) {
		for(FieldConstraint<D> c : this) {
			original = c.defaultValue(original, fixtures);
		}
		return original;
	}

	@Override
	public FieldValidationSet<D> validationSet(FieldValidationSet<D> original, Map<String, Object> fixtures) {
		for(FieldConstraint<D> c : this) {
			original = c.validationSet(original, fixtures);
		}
		return original;
	}

	
}
