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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;

import javax.swing.DebugGraphics;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.CurrentTimeService;
import uk.ac.ed.epcc.webapp.TestTimeService;
import uk.ac.ed.epcc.webapp.email.MockTansport;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.forms.html.PageHTMLForm;
import uk.ac.ed.epcc.webapp.junit4.ConfigFixtures;
import uk.ac.ed.epcc.webapp.junit4.DataBaseFixtures;
import uk.ac.ed.epcc.webapp.mock.MockServletConfig;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.PasswordAuthComposite;
import uk.ac.ed.epcc.webapp.session.PasswordUpdateFormBuilder;
import uk.ac.ed.epcc.webapp.session.SessionService;

/**
 * @author spb
 *
 */
@ConfigFixtures("password_server.properties")
public class PasswordResetServletTest extends ServletTest {

	/**
	 * 
	 */
	private static final String CORRECT_TAG = "1-49191c4u425rw2f3k103a52231x2g10";

	/**
	 * 
	 */
	public PasswordResetServletTest() {
		// TODO Auto-generated constructor stub
	}
	@Override
	public void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		servlet=new PasswordChangeRequestServlet();
		MockServletConfig config = new MockServletConfig(serv_ctx, "PasswordChangeRequestServlet");
		servlet.init(config);
		req.servlet_path="PasswordChangeRequesetServlet";
		TestTimeService t = new TestTimeService();
		Calendar c = Calendar.getInstance();
		c.set(2019, Calendar.FEBRUARY, 6);
		t.setResult(c.getTime());
		ctx.setService(t);
	}
	
	@Test
	@DataBaseFixtures("new_password_from_server.xml")
	public void testPasswordChange() throws ConsistencyError, Exception{
		takeBaseline();
		MockTansport.clear();
		req.path_info=CORRECT_TAG;
		doPost();
		checkForward("/scripts/password_change_request.jsp");
		addParam(PasswordUpdateFormBuilder.NEW_PASSWORD1,"BorisTheSpider");
		addParam(PasswordUpdateFormBuilder.NEW_PASSWORD2,"BorisTheSpider");
		addParam("submitted","true");
		
		setAction(PasswordUpdateFormBuilder.CHANGE_ACTION);
		doPost();
		checkMessage("password_changed");
		SessionService sess = ctx.getService(SessionService.class);
		assertTrue(sess.haveCurrentUser());
		AppUser user = sess.getCurrentPerson();
		assertEquals(1,user.getID());
		checkDiff("/cleanup.xsl", "servlet_password_reset.xml");
		PasswordAuthComposite comp = (PasswordAuthComposite) sess.getLoginFactory().getComposite(PasswordAuthComposite.class);
		assertTrue(comp.checkPassword(user, "BorisTheSpider"));
		assertEquals(1, MockTansport.nSent());
		assertEquals(ctx.expandText("${service.name} Password has been changed"),MockTansport.getMessage(0).getSubject());
	}
	@Test
	@DataBaseFixtures("new_password_from_server.xml")
	public void testExpired() throws ConsistencyError, Exception{
		takeBaseline();
		MockTansport.clear();
		TestTimeService t = (TestTimeService) ctx.getService(CurrentTimeService.class);
		Calendar c = Calendar.getInstance();
		c.set(2019, Calendar.JULY, 6);
		t.setResult(c.getTime());
		req.path_info=CORRECT_TAG;
		doPost();
		
		checkMessage("request_expired");
	}
	
	@Test
	@DataBaseFixtures("new_password_from_server.xml")
	public void testPasswordCancel() throws ConsistencyError, Exception{
		takeBaseline();
		MockTansport.clear();
		req.path_info=CORRECT_TAG;
		doPost();
		checkForward("/scripts/password_change_request.jsp");
		addParam(PasswordUpdateFormBuilder.NEW_PASSWORD1,"BorisTheSpider");
		addParam(PasswordUpdateFormBuilder.NEW_PASSWORD2,"BorisTheSpider");
		addParam("submitted","true");
		setAction(PasswordUpdateFormBuilder.CANCEL_ACTION);
		doPost();
		checkMessage("password_change_cancel");
		SessionService sess = ctx.getService(SessionService.class);
		assertFalse(sess.haveCurrentUser());
	
		checkDiff("/cleanup.xsl", "servlet_password_cancel.xml");
		PasswordAuthComposite comp = (PasswordAuthComposite) sess.getLoginFactory().getComposite(PasswordAuthComposite.class);
	}
	@Test
	@DataBaseFixtures("new_password_from_server.xml")
	public void testMisMatch() throws ConsistencyError, Exception{
		takeBaseline();
		req.path_info=CORRECT_TAG;
		doPost();
		checkForward("/scripts/password_change_request.jsp");
		addParam(PasswordUpdateFormBuilder.NEW_PASSWORD1,"BorisTheSpider");
		addParam(PasswordUpdateFormBuilder.NEW_PASSWORD2,"BorisTheRussian");
		addParam("submitted","true"); 
		doPost();
		checkForward("/scripts/password_change_request.jsp");
		PageHTMLForm form = (PageHTMLForm) req.getAttribute("Form");
		assertEquals("New Passwords don't match",form.getGeneralError());

		SessionService sess = ctx.getService(SessionService.class);
		assertFalse(sess.haveCurrentUser());
		
	}
	
	@Test
	@DataBaseFixtures("new_password_from_server.xml")
	public void testBadTag() throws ConsistencyError, Exception{
		takeBaseline();
		req.path_info="1-2515j732jc4y1n13s31xxxxxxxxxx";
		doPost();
		checkMessage("password_change_request_denied");
	}
}
