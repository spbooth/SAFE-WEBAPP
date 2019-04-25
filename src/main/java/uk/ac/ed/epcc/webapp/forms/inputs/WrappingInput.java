//| Copyright - The University of Edinburgh 2019                            |
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

import uk.ac.ed.epcc.webapp.forms.FieldValidator;
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;

/**
 * @author Stephen Booth
 *
 */
public abstract class WrappingInput<V> implements Input<V> {

	private final Input<V> nested;
	public Input<V> getNested() {
		return nested;
	}

	/**
	 * 
	 */
	public WrappingInput(Input<V> nested) {
		this.nested=nested;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.Input#getKey()
	 */
	@Override
	public String getKey() {
		return nested.getKey();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.Input#getValue()
	 */
	@Override
	public V getValue() {
		return nested.getValue();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.Input#setKey(java.lang.String)
	 */
	@Override
	public void setKey(String key) {
		nested.setKey(key);
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.Input#setValue(java.lang.Object)
	 */
	@Override
	public V setValue(V v) throws TypeError {
		return nested.setValue(v);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.Input#convert(java.lang.Object)
	 */
	@Override
	public V convert(Object v) throws TypeError {
		return nested.convert(v);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.Input#getString(java.lang.Object)
	 */
	@Override
	public String getString(V value) {
		return nested.getString(value);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.Input#getPrettyString(java.lang.Object)
	 */
	@Override
	public String getPrettyString(V value) {
		return nested.getPrettyString(value);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.Input#validate()
	 */
	@Override
	public void validate() throws FieldException {
		nested.validate();
		
	}

	

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.Input#addValidator(uk.ac.ed.epcc.webapp.forms.FieldValidator)
	 */
	@Override
	public void addValidator(FieldValidator<V> val) {
		nested.addValidator(val);
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.Input#removeValidator(uk.ac.ed.epcc.webapp.forms.FieldValidator)
	 */
	@Override
	public void removeValidator(FieldValidator<V> val) {
		nested.removeValidator(val);
		
	}

}
