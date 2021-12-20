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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
import uk.ac.ed.epcc.webapp.junit4.ConfigFixtures;
import uk.ac.ed.epcc.webapp.mock.MockServletConfig;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.AppUserFactory;
import uk.ac.ed.epcc.webapp.session.PasswordAuthComposite;
import uk.ac.ed.epcc.webapp.session.SessionService;

/**
 * @author spb
 *
 */
@ConfigFixtures("/basicauth.properties")
public class TestBasicAuth extends ServletTest {

	/**
	 * 
	 */
	private static final String PW = "BONG";
	/**
	 * 
	 */
	private static final String USER = "fred@example.com";

	/**
	 * 
	 */
	public TestBasicAuth() {
		
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
		assertEquals(HttpServletResponse.SC_UNAUTHORIZED, res.error);
		assertTrue(res.containsHeader("WWW-Authenticate"));
	}
	
	@Test
	public void testAuth() throws ServletException, IOException, DataFault, ParseException {
		SessionService sess = ctx.getService(SessionService.class);
		AppUserFactory fac = sess.getLoginFactory();
		
		PasswordAuthComposite comp = (PasswordAuthComposite) fac.getComposite(PasswordAuthComposite.class);
		AppUser user = fac.makeFromString(USER);
		comp.setPassword(user, PW);
		user.commit();
		String d = USER+":"+PW;
		req.header.put("Authorization", "Basic "+Base64.encodeBase64String(d.getBytes()));
		
		doPost();
		assertEquals(HttpServletResponse.SC_OK, res.error);
		assertEquals("Hello", res.stream.toString().trim());
		sess = ctx.getService(SessionService.class);
		assertTrue(sess.haveCurrentUser());
	}
}
