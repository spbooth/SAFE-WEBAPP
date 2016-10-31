//| Copyright - The University of Edinburgh 2016                            |
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
package uk.ac.ed.epcc.webapp.servlet;

import static org.junit.Assert.*;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.email.MockTansport;
import uk.ac.ed.epcc.webapp.junit4.ConfigFixtures;
import uk.ac.ed.epcc.webapp.session.EmailNameFinder;

/**
 * @author spb
 *
 */
@ConfigFixtures("passowrd_auth.properties")
public class RegisterServletTest extends AbstractRegisterServletTest {

	/**
	 * 
	 */
	public RegisterServletTest() {
		// TODO Auto-generated constructor stub
	}
	@Test
	public void testRegister() throws Exception{
		MockTansport.clear();
		takeBaseline();
		addParam("form_url", "/scripts/signup.jsp");
		addParam(EmailNameFinder.EMAIL, "thing@example.com");
		doPost();
		checkMessage("signup_ok_password");
		checkDiff("/cleanup.xsl","signup.xml");
		assertEquals(1,MockTansport.nSent());
	}

}
