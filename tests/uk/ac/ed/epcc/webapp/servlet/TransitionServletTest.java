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
package uk.ac.ed.epcc.webapp.servlet;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.AppUserFactory;
import uk.ac.ed.epcc.webapp.session.SessionService;
/** Tests of the TransitionServlet itself (not transitions)
 * @author spb
 *
 */

public class TransitionServletTest extends ServletTest {
	
	@Test
	public void testNoUser() throws ServletException, IOException{
		req.servlet_path="TransitionServlet";
		req.path_info="Test/12";
		
		TransitionServlet servlet = new TransitionServlet();
		
		servlet.doPost(req,res,ctx);
		assertEquals("test/login.jsp?error=session&page=TransitionServlet/Test/12", res.redirect);
	}
	@Test
	public void testNull() throws ServletException, IOException, DataException{
		req.servlet_path="TransitionServlet";
		req.path_info="";
		SessionService service = ctx.getService(SessionService.class);
		AppUserFactory<?> fac = service.getLoginFactory();

		AppUser user = fac.makeBDO();
		String email = ctx.getInitParameter("test.email");
		user.setEmail(email);
		user.commit();
		service.setCurrentPerson(fac.findByEmail(email));
		TransitionServlet servlet = new TransitionServlet();
		
		servlet.doPost(req,res,ctx);
		assertEquals("/messages.jsp", res.forward);
		assertEquals("invalid_input", req.getAttribute("message_type"));
	}
	
	@Test
	public void testNoTarget() throws ServletException, IOException, DataException{
		req.servlet_path="TransitionServlet";
		req.path_info="Test";
		SessionService service = ctx.getService(SessionService.class);
		AppUserFactory<?> fac = service.getLoginFactory();

		AppUser user = fac.makeBDO();
		String email = ctx.getInitParameter("test.email");
		user.setEmail(email);
		user.commit();
		service.setCurrentPerson(fac.findByEmail(email));
		TransitionServlet servlet = new TransitionServlet();
		
		servlet.doPost(req,res,ctx);
		assertEquals("/messages.jsp", res.forward);
		assertEquals("invalid_input", req.getAttribute("message_type"));
		assertEquals(TestTransitionProvider.class, req.getAttribute(TransitionServlet.TRANSITION_PROVIDER_ATTR).getClass());
	}
	
	@Test
	public void testViewTarget() throws ServletException, IOException, DataException{
		req.servlet_path="TransitionServlet";
		req.path_info="Test/9";
		SessionService service = ctx.getService(SessionService.class);
		AppUserFactory<?> fac = service.getLoginFactory();

		AppUser user = fac.makeBDO();
		String email = ctx.getInitParameter("test.email");
		user.setEmail(email);
		user.commit();
		service.setCurrentPerson(fac.findByEmail(email));
		TransitionServlet servlet = new TransitionServlet();
		
		servlet.doPost(req,res,ctx);
		assertEquals("/scripts/view_target.jsp", res.forward);
		assertEquals(9, req.getAttribute(TransitionServlet.TARGET_ATTRIBUTE));
		assertEquals(TestTransitionProvider.class, req.getAttribute(TransitionServlet.TRANSITION_PROVIDER_ATTR).getClass());
	}
	@Test
	public void testForbiddenViewTarget() throws ServletException, IOException, DataException{
		req.servlet_path="TransitionServlet";
		req.path_info="Test/12";
		SessionService service = ctx.getService(SessionService.class);
		AppUserFactory<?> fac = service.getLoginFactory();

		AppUser user = fac.makeBDO();
		String email = ctx.getInitParameter("test.email");
		user.setEmail(email);
		user.commit();
		service.setCurrentPerson(fac.findByEmail(email));
		TransitionServlet servlet = new TransitionServlet();
		
		servlet.doPost(req,res,ctx);
		assertEquals("/messages.jsp", res.forward);
		assertEquals("access_denied", req.getAttribute("message_type"));
	}
	@Test
	public void testAllowDirectTransition() throws ServletException, IOException, DataException{
		req.servlet_path="TransitionServlet";
		req.path_info="Test/6";
		req.params.put(TransitionServlet.TRANSITION_KEY_ATTR, "Add");
		SessionService service = ctx.getService(SessionService.class);
		AppUserFactory<?> fac = service.getLoginFactory();

		AppUser user = fac.makeBDO();
		String email = ctx.getInitParameter("test.email");
		user.setEmail(email);
		user.commit();
		service.setCurrentPerson(fac.findByEmail(email));
		TransitionServlet servlet = new TransitionServlet();
		
		servlet.doPost(req,res,ctx);
		assertEquals("/scripts/view_target.jsp", res.forward);
		assertEquals(7, req.getAttribute(TransitionServlet.TARGET_ATTRIBUTE));
		assertEquals(TestTransitionProvider.class, req.getAttribute(TransitionServlet.TRANSITION_PROVIDER_ATTR).getClass());
	}
	@Test
	public void testDenyDirectTransition() throws ServletException, IOException, DataException{
		req.servlet_path="TransitionServlet";
		req.path_info="Test/12";
		req.params.put(TransitionServlet.TRANSITION_KEY_ATTR, "Add");
		SessionService service = ctx.getService(SessionService.class);
		AppUserFactory<?> fac = service.getLoginFactory();

		AppUser user = fac.makeBDO();
		String email = ctx.getInitParameter("test.email");
		user.setEmail(email);
		user.commit();
		service.setCurrentPerson(fac.findByEmail(email));
		TransitionServlet servlet = new TransitionServlet();
		
		servlet.doPost(req,res,ctx);
		assertEquals("/messages.jsp", res.forward);
		assertEquals("access_denied", req.getAttribute("message_type"));
		assertEquals(TestTransitionProvider.class, req.getAttribute(TransitionServlet.TRANSITION_PROVIDER_ATTR).getClass());
	}
	
	@Test
	public void testAllowFormTransition() throws ServletException, IOException, DataException{
		req.servlet_path="TransitionServlet";
		req.path_info="Test/6";
		req.params.put(TransitionServlet.TRANSITION_KEY_ATTR, "FormAdd");
		SessionService service = ctx.getService(SessionService.class);
		AppUserFactory<?> fac = service.getLoginFactory();

		AppUser user = fac.makeBDO();
		String email = ctx.getInitParameter("test.email");
		user.setEmail(email);
		user.commit();
		service.setCurrentPerson(fac.findByEmail(email));
		TransitionServlet servlet = new TransitionServlet();
		
		servlet.doPost(req,res,ctx);
		assertEquals("/scripts/transition.jsp", res.forward);
		assertEquals(6, req.getAttribute(TransitionServlet.TARGET_ATTRIBUTE));
		assertEquals(TestTransitionProvider.class, req.getAttribute(TransitionServlet.TRANSITION_PROVIDER_ATTR).getClass());
	}
	@Test
	public void testAllowPostFormTransition() throws ServletException, IOException, DataException{
		req.servlet_path="TransitionServlet";
		req.path_info="Test/6";
		req.params.put(TransitionServlet.TRANSITION_KEY_ATTR, "FormAdd");
		req.params.put("transition_form", "true");
		req.params.put("Value","2");
		req.params.put("Add","Add");
		req.params.put("form_url","/scripts/transition.jsp");
		SessionService service = ctx.getService(SessionService.class);
		AppUserFactory<?> fac = service.getLoginFactory();

		AppUser user = fac.makeBDO();
		String email = ctx.getInitParameter("test.email");
		user.setEmail(email);
		user.commit();
		service.setCurrentPerson(fac.findByEmail(email));
		TransitionServlet servlet = new TransitionServlet();
		
		servlet.doPost(req,res,ctx);
		assertEquals("/scripts/view_target.jsp", res.forward);
		assertEquals(8, req.getAttribute(TransitionServlet.TARGET_ATTRIBUTE));
		assertEquals(TestTransitionProvider.class, req.getAttribute(TransitionServlet.TRANSITION_PROVIDER_ATTR).getClass());
	}
	
	@Test
	public void testDenyFormTransition() throws ServletException, IOException, DataException{
		req.servlet_path="TransitionServlet";
		req.path_info="Test/12";
		req.params.put(TransitionServlet.TRANSITION_KEY_ATTR, "FormAdd");
		SessionService service = ctx.getService(SessionService.class);
		AppUserFactory<?> fac = service.getLoginFactory();

		AppUser user = fac.makeBDO();
		String email = ctx.getInitParameter("test.email");
		user.setEmail(email);
		user.commit();
		service.setCurrentPerson(fac.findByEmail(email));
		TransitionServlet servlet = new TransitionServlet();
		
		servlet.doPost(req,res,ctx);
		assertEquals("/messages.jsp", res.forward);
		assertEquals("access_denied", req.getAttribute("message_type"));
		assertEquals(TestTransitionProvider.class, req.getAttribute(TransitionServlet.TRANSITION_PROVIDER_ATTR).getClass());
	}
	@Test
	public void testDenyPostFormTransition() throws ServletException, IOException, DataException{
		req.servlet_path="TransitionServlet";
		req.path_info="Test/12";
		req.params.put(TransitionServlet.TRANSITION_KEY_ATTR, "FormAdd");
		req.params.put("transition_form", "true");
		req.params.put("Value","2");
		req.params.put("Add","Add");
		SessionService service = ctx.getService(SessionService.class);
		AppUserFactory<?> fac = service.getLoginFactory();

		AppUser user = fac.makeBDO();
		String email = ctx.getInitParameter("test.email");
		user.setEmail(email);
		user.commit();
		service.setCurrentPerson(fac.findByEmail(email));
		TransitionServlet servlet = new TransitionServlet();
		
		servlet.doPost(req,res,ctx);
		assertEquals("/messages.jsp", res.forward);
		assertEquals("access_denied", req.getAttribute("message_type"));
		assertEquals(TestTransitionProvider.class, req.getAttribute(TransitionServlet.TRANSITION_PROVIDER_ATTR).getClass());
	}
}