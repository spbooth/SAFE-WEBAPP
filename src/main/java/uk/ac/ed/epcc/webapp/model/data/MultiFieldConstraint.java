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

import java.util.LinkedHashSet;

import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.model.data.forms.Selector;



/**
 * @author Stephen Booth
 *
 */
public class MultiFieldConstraint extends LinkedHashSet<FieldConstraint> implements FieldConstraint{
	
	/**
	 * 
	 */
	public MultiFieldConstraint(FieldConstraint ...constraints) {
		super();
		for(FieldConstraint c : constraints) {
			add(c);
		}
	}

	@Override
	public boolean add(FieldConstraint e) {
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

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.FieldConstraint#apply(boolean, java.lang.String, uk.ac.ed.epcc.webapp.model.data.forms.Selector, uk.ac.ed.epcc.webapp.forms.Form)
	 */
	@Override
	public <I extends Input> Selector<I> apply(boolean support_multi_stage, String field, Selector<I> sel,
			Form form) {
		for(FieldConstraint c: this) {
			sel = c.apply(support_multi_stage, field, sel, form);
			if( sel == null ) {
				return null;
			}
		}
		return sel;
	}

	
}
