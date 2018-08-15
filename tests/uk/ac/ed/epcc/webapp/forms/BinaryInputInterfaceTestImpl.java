//| Copyright - The University of Edinburgh 2015                            |
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;



import uk.ac.ed.epcc.webapp.forms.inputs.BinaryInput;


public class BinaryInputInterfaceTestImpl<T,I extends BinaryInput<T>,X extends TestDataProvider<T, I>> implements BinaryInputInterfaceTest<T,I,X>  {

	private X target;
	public BinaryInputInterfaceTestImpl(X target){
		this.target=target;
	}

	public void testBinary() throws Exception{
		I i = target.getInput();
		assertNotNull(i.getChecked());
		
		i.setChecked(true);
		T checked_value = i.getValue();
		assertTrue(i.isChecked());
		i.setChecked(false);
		T unchecked_value = i.getValue();
		assertFalse(i.isChecked());
		
		assertNotEquals(checked_value, unchecked_value);
		i.setValue(checked_value);
		assertTrue(i.isChecked());
		
		i.setValue(unchecked_value);
		assertFalse(i.isChecked());
		
	}

}