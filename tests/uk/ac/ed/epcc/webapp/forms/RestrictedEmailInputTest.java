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

import java.util.Set;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.email.inputs.EmailInput;
import uk.ac.ed.epcc.webapp.email.inputs.RestrictedEmailInput;


public class RestrictedEmailInputTest extends EmailInputTest {
	@Test
	public void dummy(){
		
	}
	
	@Override
	public EmailInput getInput() {
		return  new RestrictedEmailInput("gmail,yahoo,hotmail");
	}
	
	@Override
	public Set<String> getBadData() {
		
	
		Set<String> badData = super.getBadData();
		badData.add("fred@gmail.com");
		badData.add("fred@hotmail.com");
		badData.add("fred@yahoo.com");
		return badData;
	}
}