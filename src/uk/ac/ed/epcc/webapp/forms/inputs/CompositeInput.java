//| Copyright - The University of Edinburgh 2011                            |
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
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
/**  A selector that is a composite combination of other Selectors.
 * 
 */
package uk.ac.ed.epcc.webapp.forms.inputs;

import java.util.Iterator;

import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;

/** Composite input is a potentially optional MultiInput where each of the
 * Inputs has to validate independently
 * 
 * @author spb
 * @param <V> type of result object
 */
public abstract class CompositeInput<V> extends MultiInput<V,Input> implements Input<V>, OptionalInput {

	

	boolean optional = false;

	public CompositeInput() {
		super();
		
	}

	@Override
	public final void addInput(String sub_key, String label, Input i) {
		super.addInput(sub_key,label,i);
		if (i instanceof OptionalInput) {
			((OptionalInput) i).setOptional(isOptional());
		}
	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.epcc.webapp.model.data.forms.OptionalInput#isOptional()
	 */
	public final boolean isOptional() {
		return optional;
	}

	

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.epcc.webapp.model.data.forms.OptionalInput#setOptional(boolean)
	 */
	public void setOptional(boolean opt) {
		optional = opt;
		for (Iterator<Input> it = getInputs(); it.hasNext();) {
			
			Input i = it.next();
			if (i instanceof OptionalInput) {
				((OptionalInput) i).setOptional(opt);
			}
		}
	}



	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.epcc.webapp.model.data.forms.Selector#validate()
	 */
	public void validate() throws FieldException {
		for (Iterator it = getInputs(); it.hasNext();) {
			Input s = (Input) it.next();
			s.validate();
		}
	}
}