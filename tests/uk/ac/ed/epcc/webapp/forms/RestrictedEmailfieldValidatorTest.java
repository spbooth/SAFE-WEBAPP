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

import static org.junit.Assert.fail;

import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.email.inputs.RestrictedEmailFieldValidator;
import uk.ac.ed.epcc.webapp.forms.exceptions.ValidateException;


public class RestrictedEmailfieldValidatorTest extends WebappTestBase {
	
	
	public void testBadAddresses() throws ValidateException{
		RestrictedEmailFieldValidator val = new RestrictedEmailFieldValidator("gmail,yahoo,hotmail");
		
		for(String bad : new String[]{ "fred@gmail.com","fred@hotmail.com", "fred@yahoo.com"}){
			try{
				val.validate(bad);
				fail("Passed bad email "+bad);
			}catch(ValidateException e){
				
			}
		}
		for(String good : new String[]{ "fred@exmple.com","fred@ed.ac.uk"}){
			
				val.validate(good);
				
		}
	}
	
	
	
}