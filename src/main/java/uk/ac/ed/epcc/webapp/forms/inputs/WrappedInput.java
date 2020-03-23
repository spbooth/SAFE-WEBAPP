//| Copyright - The University of Edinburgh 2017                            |
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

/** Interface for inputs that should be wrapped in a div element in html
 * @author spb
 *
 */
public class WrappedInput<X> implements Input<X> {

	/**
	 * @param input
	 * @param my_class
	 */
	public WrappedInput(Input<X> input, String my_class) {
		super();
		this.input = input;
		this.my_class = my_class;
	}


	private final Input<X> input;
	private final String my_class;
	
	
	/** class for the enclosing div element.
	 * 
	 * @return
	 */
	public String getWrapperClass() {
		return my_class;
	}
	
	public Input getWrappedInput() {
		return input;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.Input#getKey()
	 */
	@Override
	public String getKey() {
		return input.getKey();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.Input#getValue()
	 */
	@Override
	public X getValue() {
		return input.getValue();
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.Input#setKey(java.lang.String)
	 */
	@Override
	public void setKey(String key) {
		input.setKey(key);
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.Input#setValue(java.lang.Object)
	 */
	@Override
	public X setValue(X v) throws TypeError {
		return input.setValue(v);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.Input#convert(java.lang.Object)
	 */
	@Override
	public X convert(Object v) throws TypeError {
		return input.convert(v);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.Input#getString(java.lang.Object)
	 */
	@Override
	public String getString(X value) {
		return input.getString(value);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.Input#getPrettyString(java.lang.Object)
	 */
	@Override
	public String getPrettyString(X value) {
		return input.getPrettyString(value);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.Input#validate()
	 */
	@Override
	public void validate() throws FieldException {
		input.validate();
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.Input#accept(uk.ac.ed.epcc.webapp.forms.inputs.InputVisitor)
	 */
	@Override
	public <R> R accept(InputVisitor<R> vis) throws Exception {
		return vis.visitWrappedInput(this);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.Input#addValidator(uk.ac.ed.epcc.webapp.forms.FieldValidator)
	 */
	@Override
	public void addValidator(FieldValidator<X> val) {
		input.addValidator(val);
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.Input#removeValidator(uk.ac.ed.epcc.webapp.forms.FieldValidator)
	 */
	@Override
	public void removeValidator(FieldValidator<X> val) {
		input.removeValidator(val);
		
	}

	
}
