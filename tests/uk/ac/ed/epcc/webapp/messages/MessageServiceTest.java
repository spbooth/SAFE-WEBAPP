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
package uk.ac.ed.epcc.webapp.messages;

import static org.junit.Assert.assertEquals;

import java.util.ResourceBundle;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.WebappTestBase;
/**
 * @author spb
 *
 */

public class MessageServiceTest extends WebappTestBase {

	
	
	@Test
	public void testMessageService(){
		MessageBundleService serv = getContext().getService(MessageBundleService.class);
		
		ResourceBundle messages = serv.getBundle();
		assertEquals("This is A",messages.getString("a.title"));
		assertEquals("This is B",messages.getString("b.title"));
		assertEquals("I am A",messages.getString("a.text"));
		// check localisation still works
		assertEquals("Hello I am called B",messages.getString("b.text"));
	}
	

}