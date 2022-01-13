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
package uk.ac.ed.epcc.webapp.session;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.junit4.ConfigFixtures;
import uk.ac.ed.epcc.webapp.junit4.DataBaseFixtures;
import uk.ac.ed.epcc.webapp.mock.MockOutputStream;
import uk.ac.ed.epcc.webapp.mock.MockServletConfig;
import uk.ac.ed.epcc.webapp.servlet.ServeDataServlet;
import uk.ac.ed.epcc.webapp.servlet.ServletTest;
import uk.ac.ed.epcc.webapp.servlet.TransitionServlet;

/**
 * @author Stephen Booth
 *
 */
@DataBaseFixtures("avatar.xml")
@ConfigFixtures("avatar.properties")
public class ServeAvatarTest extends ServletTest{

	@Before
	public void setup() throws ServletException {
		servlet=new ServeDataServlet();
		MockServletConfig config = new MockServletConfig(serv_ctx, "ServeDataServlet");
		servlet.init(config);
		req.servlet_path=ServeDataServlet.DATA_PATH;
	}
	
	@Test
	public void testServeAvatar() throws DataException, ServletException, IOException {
		SessionService  sess = setupPerson("sw@example.com");
		AppUser target = sess.getCurrentPerson();
		req.path_info="Avatar/"+target.getID()+"/avatar.png";
		doPost();
		assertEquals("image/png", res.content_type);
		assertTrue(res.stream.isClosed());
		MockOutputStream stream = (MockOutputStream) res.getOutputStream();
		//writeFile("avatar2.png",stream.getData());
		byte[] data = stream.getData();
		byte[] expected = getResourceAsBytes("avatar2.png");
		assertEquals(expected.length,data.length);
		for(int i=0;i<data.length;i++) {
			assertEquals("byte "+i,  expected[i], data[i]);
		}
	}
	
	@Test
	public void testServeOther() throws ServletException, IOException, DataException {
		SessionService  sess = setupPerson("evil@example.com");
		AppUser target = sess.getCurrentPerson();
		req.path_info="Avatar/"+target.getID()+"/avatar.png";
		doPost();
		assertEquals(HttpServletResponse.SC_FORBIDDEN, res.error);
	}
}
