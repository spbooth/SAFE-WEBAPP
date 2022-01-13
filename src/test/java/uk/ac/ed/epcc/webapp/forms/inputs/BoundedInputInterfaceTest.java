//| Copyright - The University of Edinburgh 2018                            |
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

import org.junit.Test;

/**
 * @author Stephen Booth
 *
 */
public interface BoundedInputInterfaceTest<T, I extends BoundedInput<T>,X extends BoundedInputDataProvider<T, I>> {
	@Test
    public void testMin()throws Exception;
	@Test
    public void testMax()throws Exception;
	@Test
    public void testBoth()throws Exception;
	
	@Test
    public void testAll() throws Exception;
}
