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

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.junit4.ConfigFixtures;
import uk.ac.ed.epcc.webapp.servlet.session.ServletSessionService;

/**
 * @author spb
 *
 */
public class SimpleHeartbeatServletTest extends HeartbeatServletTest {

	/**
	 * 
	 */
	public SimpleHeartbeatServletTest() {
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.servlet.HeartbeatServletTest#makeService()
	 */
	@Override
	public ServletSessionService makeService() {
		return new ServletSessionService(ctx);
	}

	@Test
	public void testNoAuth() throws ServletException, IOException{
		doPost();
		assertEquals(HttpServletResponse.SC_FORBIDDEN,res.error);
		assertEquals("Not Authenticated",res.error_str);
	}
	
	@Test
	@ConfigFixtures("mock_heartbeat.properties")
	public void testMock() throws ServletException, IOException{
		
		MockHeartbeatListener.has_run=false;
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, 1);
		MockHeartbeatListener.date=c.getTime();
		
		req.remote_user="fred";
		doPost();
		assertTrue(MockHeartbeatListener.has_run);
		assertEquals(c.getTime(), MockHeartbeatListener.date);
		
	}
}
