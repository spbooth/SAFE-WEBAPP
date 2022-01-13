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
import uk.ac.ed.epcc.webapp.session.AppUserFactory;
import uk.ac.ed.epcc.webapp.session.PasswordAuthComposite;
import uk.ac.ed.epcc.webapp.session.PasswordChangeRequestFactory;
import uk.ac.ed.epcc.webapp.session.PasswordUpdateFormBuilder;
import uk.ac.ed.epcc.webapp.session.SessionService;
import uk.ac.ed.epcc.webapp.session.twofactor.FormAuthComposite;
import uk.ac.ed.epcc.webapp.session.twofactor.TotpCodeAuthComposite;

/**
 * @author spb
 *
 */
@ConfigFixtures({"password_server.properties","twofactor.properties"})
public class PasswordResetServletTest extends ServletTest {

	/**
	 * 
	 */
	private static final String CORRECT_TAG = "1-4m26w232f6c2i4n3w361m3u4r5j343e6m495505v1k5t4f5d6f73b73h3uu";

	/**
	 * 
	 */
	public PasswordResetServletTest() {
		
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
	public void testNoPurge() throws Exception {
		takeBaseline();
		PasswordChangeRequestFactory fac = new PasswordChangeRequestFactory<>(ctx.getService(SessionService.class).getLoginFactory());
		fac.purge();
		checkUnchanged();
	}
	
	@Test
	@DataBaseFixtures("new_password_from_server.xml")
	public void testPurge() throws Exception {
		takeBaseline();
		TestTimeService t = (TestTimeService) ctx.getService(CurrentTimeService.class);
		Calendar c = Calendar.getInstance();
		c.set(2020, Calendar.JULY, 6);
		t.setResult(c.getTime());
		PasswordChangeRequestFactory fac = new PasswordChangeRequestFactory<>(ctx.getService(SessionService.class).getLoginFactory());
		fac.purge();
		checkDiff("/cleanup.xsl", "purged.xml");
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
	
	public void testPasswordChangeWith2FA() throws ConsistencyError, Exception{
		// setup two factor
		
		SessionService sess = ctx.getService(SessionService.class);
		AppUserFactory fac = sess.getLoginFactory();
		AppUser user = (AppUser) fac.find(1);
		TotpCodeAuthComposite ta = (TotpCodeAuthComposite) fac.getComposite(FormAuthComposite.class);
		String external = "UJ4SLJJPNPXVGIPLXDTQUGVKNI";
		ta.setSecret(user, external);
		user.commit();
		
		
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
		
		assertFalse(sess.haveCurrentUser());
		
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
		c.set(2020, Calendar.JULY, 16);
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
		addParam(PasswordUpdateFormBuilder.NEW_PASSWORD2,"X");
		addParam("submitted","true");
		setAction(PasswordUpdateFormBuilder.CANCEL_ACTION);
		doPost();
		checkRedirect("/login.jsp");
		//checkMessage("password_change_cancel");
		SessionService sess = ctx.getService(SessionService.class);
		assertFalse(sess.haveCurrentUser());
		checkUnchanged();
		
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
