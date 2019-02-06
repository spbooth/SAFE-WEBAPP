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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.mail.Message;
import javax.servlet.ServletException;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.email.MockTansport;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.junit4.DataBaseFixtures;
import uk.ac.ed.epcc.webapp.mock.MockServletConfig;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.AppUserFactory;
import uk.ac.ed.epcc.webapp.session.EmailChangeRequestFactory;
import uk.ac.ed.epcc.webapp.session.EmailNameFinder;
import uk.ac.ed.epcc.webapp.session.PasswordAuthComposite;
import uk.ac.ed.epcc.webapp.session.SessionService;

/**
 * @author spb
 *
 */
public class EmailChangeServletTest<A extends AppUser> extends ServletTest {

	/**
	 * 
	 */
	public EmailChangeServletTest() {
	}
	@Override
	public void setUp() throws Exception {
		
		super.setUp();
		servlet=new EmailChangeRequestServlet();
		MockServletConfig config = new MockServletConfig(serv_ctx, "EmailChangeRequestServlet");
		servlet.init(config);
		req.servlet_path="EmailChangeRequestServlet";
	}
	
	
	
	@Test
	@DataBaseFixtures("email_change.xml")
	public void testComplete() throws ConsistencyError, Exception{
		MockTansport.clear();
		takeBaseline();
	
		AppUserFactory<A> fac = ctx.getService(SessionService.class).getLoginFactory();
		A user =  fac.findByEmail("fred@example.com");
		SessionService<A> sess = ctx.getService(SessionService.class);
		sess.setCurrentPerson(user);
		req.path_info="1-2u5t433z3f3i2o28114j6g133e503y6y";
		doPost();
		checkMessage("email_change_request_successful");
		assertEquals("bilbo@example.com",fac.find(user.getID()).getEmail());
		checkDiff("/cleanup.xsl", "email_change_complete.xml");
	
	}
	
	@Test
	@DataBaseFixtures("email_change.xml")
	public void testCompleteWrongTag() throws ConsistencyError, Exception{
		MockTansport.clear();
		takeBaseline();
	
		AppUserFactory<A> fac = ctx.getService(SessionService.class).getLoginFactory();
		A user =  fac.findByEmail("fred@example.com");
		SessionService<A> sess = ctx.getService(SessionService.class);
		sess.setCurrentPerson(user);
		req.path_info="1-2u5t433z3f3i2o28114j6g133e503XXX";
		doPost();
		checkMessage("email_change_request_denied");
		
	
	}
	@Test
	@DataBaseFixtures("email_change.xml")
	public void testCompleteNoUser() throws ConsistencyError, Exception{
		MockTansport.clear();
		takeBaseline();
	
		AppUserFactory<A> fac = ctx.getService(SessionService.class).getLoginFactory();
		A user =  fac.findByEmail("fred@example.com");
		SessionService<A> sess = ctx.getService(SessionService.class);
		
		req.path_info="1-2u5t433z3f3i2o28114j6g133e503y6y";
		doPost();
		checkForward("/login.jsp");
	
	}
	@Test
	@DataBaseFixtures("email_change.xml")
	public void testCompleteWrongUser() throws ConsistencyError, Exception{
		MockTansport.clear();
		takeBaseline();
	
		//AppUserFactory<A> fac = ctx.getService(SessionService.class).getLoginFactory();
	
		SessionService<A> sess = setupPerson("bill@example.com");
		
		req.path_info="1-2u5t433z3f3i2o28114j6g133e503y6y";
		doPost();
		checkMessage("email_change_request_denied");
		
	
	}
}
