//| Copyright - The University of Edinburgh 2013                            |
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
package uk.ac.ed.epcc.webapp.forms;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.ContextHolder;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
/**
 * @author spb
 *
 */

public interface UnmodifiableInputInterfaceTest<T,I extends Input<T>,X extends TestDataProvider<T,I> & ContextHolder> {

	@Test
	public void testWebParse() throws Exception;
		
		
}