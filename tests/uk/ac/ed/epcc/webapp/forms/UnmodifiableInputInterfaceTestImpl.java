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

import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;


import uk.ac.ed.epcc.webapp.ContextHolder;
import uk.ac.ed.epcc.webapp.forms.html.HTMLForm;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import static org.junit.Assert.*;
/**
 * @author spb
 *
 */

@Ignore
public class UnmodifiableInputInterfaceTestImpl<T,I extends Input<T>,X extends TestDataProvider<T,I> & ContextHolder> implements UnmodifiableInputInterfaceTest<T,I,X>{

	private X target;
	/**
	 * 
	 */
	public UnmodifiableInputInterfaceTestImpl(X target) {
		this.target=target;
	}

	@Override
	@Test
	public void testWebParse() throws Exception{
		I input = target.getInput();
		
		HTMLForm f = new HTMLForm(target.getContext());
		f.addInput("input", "input", input);
		Map<String,Object> params = new HashMap<>();
		Map<String,String> errors = new HashMap<>();
		params.put("input", "BadValue");
		for(T data : target.getGoodData()){
			input.setValue(data);
			
			f.parsePost(errors, params, true);
			assertEquals(0, errors.size());
			assertEquals(data, f.get("input"));
		}
		
		
	}
}