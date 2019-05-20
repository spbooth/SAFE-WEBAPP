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
package uk.ac.ed.epcc.webapp.servlet;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.junit4.ConfigFixtures;
import uk.ac.ed.epcc.webapp.mock.MockServletConfig;
import uk.ac.ed.epcc.webapp.session.SessionService;

/**
 * @author spb
 *
 */
@ConfigFixtures("/auto_create.properties")
public class TestAutoCreate extends ServletTest {

	/**
	 * 
	 */
	public TestAutoCreate() {
		// TODO Auto-generated constructor stub
	}
	@Override
	public void setUp() throws Exception {
		
		super.setUp();
		servlet=new TestSessionServlet();
		MockServletConfig config = new MockServletConfig(serv_ctx, "TestSessionServlet");
		servlet.init(config);
		req.servlet_path="TestSessionServlet";
	}
	
	@Test
	public void testNoAuth() throws ServletException, IOException{
		doPost();
		checkMessage("access_denied");
	}
	
	@Test
	public void testNoRegister() throws ConsistencyError, Exception{
		ctx.getService(SessionService.class).getLoginFactory();
		takeBaseline();
		req.remote_user="fred";
		doPost();
		// This should create a record but still redirect to register
		checkRequestAuth("TestSessionServlet/");
		checkDiff("/cleanup.xsl", "noregister.xml");
	}
	
}
