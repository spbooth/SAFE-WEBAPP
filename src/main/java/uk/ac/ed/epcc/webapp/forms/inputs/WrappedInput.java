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

/** Interface for inputs that should be wrapped in a div element in html
 * @author spb
 *
 */
public class WrappedInput<X> extends WrappingInput<X> {

	/**
	 * @param input
	 * @param my_class
	 */
	public WrappedInput(Input<X> input, String my_class) {
		super(input);
		this.my_class = my_class;
	}

	private final String my_class;
	
	
	/** class for the enclosing div element.
	 * 
	 * @return
	 */
	public String getWrapperClass() {
		return my_class;
	}
	

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.Input#accept(uk.ac.ed.epcc.webapp.forms.inputs.InputVisitor)
	 */
	@Override
	public <R> R accept(InputVisitor<R> vis) throws Exception {
		return vis.visitWrappedInput(this);
	}

	
}
