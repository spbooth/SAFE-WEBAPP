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
import static org.junit.Assert.assertFalse;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.AppUserFactory;
import uk.ac.ed.epcc.webapp.session.SessionService;
/** Tests of the TransitionServlet itself (not transitions)
 * @author spb
 *
 */

public final class BasicTransitionServletTest extends AbstractTransitionServletTest {
	
	@Test
	public void testNoUser() throws ServletException, IOException, TransitionException{
		TestTransitionProvider provider = new TestTransitionProvider(ctx);
		setTransition(provider, null, 12);
		runTransition();
		checkRedirect("/login.jsp?error=session&page=TransitionServlet/Test/12");
	}
	/**
	 * @throws DataFault
	 * @throws DataException
	 */
	private SessionService setupUser() throws DataFault, DataException {
		return setupPerson(ctx.getInitParameter("test.email"));
	}
	@Test
	public void testNull() throws ServletException, IOException, DataException{
		
		setupUser();
		runTransition();
		checkMessage("invalid_input");
	}
	
	
	@Test
	public void testNoTarget() throws ServletException, IOException, DataException, TransitionException{
		setupUser();
		TestTransitionProvider provider = new TestTransitionProvider(ctx);
		setTransition(provider, null, null);
		runTransition();
		checkMessage("invalid_input");
	}
	
	@Test
	public void testViewTarget() throws ServletException, IOException, DataException, TransitionException{
		setupUser();
		TestTransitionProvider provider = new TestTransitionProvider(ctx);
		setTransition(provider, null, 9);
		runTransition();
		checkViewForward(provider, 9);
		
	}
	@Test
	public void testForbiddenViewTarget() throws ServletException, IOException, DataException, TransitionException{
		setupUser();
		TestTransitionProvider provider = new TestTransitionProvider(ctx);
		setTransition(provider, null, 12);
		runTransition();
		checkMessage("access_denied");
	}
	@Test
	public void testAllowDirectTransition() throws ServletException, IOException, DataException, TransitionException{
		setupUser();
		TestTransitionProvider provider = new TestTransitionProvider(ctx);
		setTransition(provider, TestTransitionProvider.ADD_KEY, 6);
		runTransition();
		checkViewForward(provider, 7);	// Its a forward becasue the provider uses chain not a viewresult
	}
	@Test
	public void testDenyDirectTransition() throws ServletException, IOException, DataException, TransitionException{
		setupUser();
		TestTransitionProvider provider = new TestTransitionProvider(ctx);
		setTransition(provider, TestTransitionProvider.ADD_KEY, 12);
		runTransition();
		checkMessage("access_denied");
	}
	
	@Test
	public void testAllowFormTransition() throws ServletException, IOException, DataException, TransitionException{
		setupUser();
		TestTransitionProvider provider = new TestTransitionProvider(ctx);
		setTransition(provider, TestTransitionProvider.FORM_ADD_KEY, 6,false);
		runTransition();
		// forward to form
		checkForwardToTransition(provider, TestTransitionProvider.FORM_ADD_KEY, 6);
		
		
		addParam(TestTransitionProvider.VALUE, 2);
		runTransition();
		checkViewForward(provider, 8);
	}
	@Test
	public void testFormTransitionBadCsrf() throws ServletException, IOException, DataException, TransitionException{
		SessionService sess = setupUser();
		TestTransitionProvider provider = new TestTransitionProvider(ctx);
		setTransition(provider, TestTransitionProvider.FORM_ADD_KEY, 6);
		addParam(TransitionServlet.TRANSITION_CSRF_ATTR, "womble");
		runTransition();
		checkMessage("crsf_check_failed");
		assertFalse(ctx.getService(SessionService.class).haveCurrentUser());
	}
	@Test
	public void testFormTransitionNoCsrf() throws ServletException, IOException, DataException, TransitionException{
		SessionService sess = setupUser();
		TestTransitionProvider provider = new TestTransitionProvider(ctx);
		setTransition(provider, TestTransitionProvider.FORM_ADD_KEY, 6);
		clearParam(TransitionServlet.TRANSITION_CSRF_ATTR);
		runTransition();
		checkMessage("crsf_check_failed");
		assertFalse(ctx.getService(SessionService.class).haveCurrentUser());
	}
	@Test
	public void testDenyFormTransition() throws ServletException, IOException, DataException, TransitionException{
		setupUser();
		TestTransitionProvider provider = new TestTransitionProvider(ctx);
		setTransition(provider, TestTransitionProvider.FORM_ADD_KEY, 12,false);
		runTransition();
		checkMessage("access_denied");
	}
	@Test
	public void testDenyPostFormTransition() throws ServletException, IOException, DataException, TransitionException{
		setupUser();
		TestTransitionProvider provider = new TestTransitionProvider(ctx);
		setTransition(provider, TestTransitionProvider.FORM_ADD_KEY, 12);
		runTransition();
		checkMessage("access_denied");
	}
}