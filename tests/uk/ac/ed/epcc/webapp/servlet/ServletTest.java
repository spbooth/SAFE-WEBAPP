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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;

import org.junit.After;
import org.junit.Before;
import org.xml.sax.InputSource;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Indexed;
import uk.ac.ed.epcc.webapp.TestDataHelper;
import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.config.ConfigService;
import uk.ac.ed.epcc.webapp.content.XMLPrinter;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.mock.MockRequest;
import uk.ac.ed.epcc.webapp.mock.MockResponse;
import uk.ac.ed.epcc.webapp.mock.MockServletContext;
import uk.ac.ed.epcc.webapp.mock.MockSession;
import uk.ac.ed.epcc.webapp.model.ClassificationFactory;
import uk.ac.ed.epcc.webapp.model.data.Dumper;
import uk.ac.ed.epcc.webapp.model.data.XMLDataUtils;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.stream.MimeStreamData;
import uk.ac.ed.epcc.webapp.servlet.session.ServletSessionService;
import uk.ac.ed.epcc.webapp.session.AbstractSessionService;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.AppUserFactory;
import uk.ac.ed.epcc.webapp.session.SessionService;
import static org.junit.Assert.*;
/** This is an abstract test class for performing high
 * level tests of operations against servlets.
 * <p>
 * Mock objects are used to pass {@link HttpServletRequest} and
 * {@link HttpServletResponse} objects into the servlet.
 * Additional utility methods provide re-usable code for
 * modifying the request or checking the state of the response.
 * These can be used to simplify the setup of specific tests.
 * <p>
 * In addition a {@link XMLDataUtils} class is included to perform
 * dumps of changes in database state. The intension is that each test should represent a
 * single form-post to the servlet and a XML diff of the database state can be taken and
 * compared with an expected result.
 *  
 * @author spb
 *
 */


public abstract class ServletTest extends WebappTestBase{

	protected WebappServlet servlet;
	 /**
	 * 
	 */
	private static final String TEST_APP_NAME = "test";
	public MockRequest req=null;
	public MockResponse res=null;
	public MockServletContext serv_ctx;
	
	
	
	
	/** setup the mock objects for a test
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		
		MockServletContext mockServletContext = new MockServletContext();
		//configure the test props through the MockServletContext
		mockServletContext.setProps(ctx.getService(ConfigService.class).getServiceProperties());
		serv_ctx=mockServletContext;
		
		resetRequest();
		
	}

	/** Reset to request/response objects so that a new request can be sent to the servlet.
	 * If a session is in place (the usual case) then this is preserved in the new request. 
	 * toggle-roles (and any temp-roles set as part of the test) are also preserved.
	 */
	protected void resetRequest() {
		MockSession session=null;
		if( req != null){
			session=req.session;
		}
		res = new MockResponse();
		req= new MockRequest(TEST_APP_NAME);
		req.session=session;
	
		
		ctx.setService(new Servlet3MultiPartServletService(ctx, serv_ctx, req, res));
		SessionService<?> old_service = ctx.getService(SessionService.class);
		AppUser user = old_service.getCurrentPerson();
        // As the session is preserved this will preserve the logged in person toggle-roles etc.
		ServletSessionService service = new ServletSessionService(ctx);
		ctx.setService(service);

		
		// fake up error filter
		req.setAttribute(ErrorFilter.APP_CONTEXT_ATTR, ctx);
	}
	@After
	public void tearDown() throws Exception {
		serv_ctx=null;
		res=null;
		req=null;
		ctx.clearService(ServletService.class);
	}
	
	/** Checks that the result is a redirect to the specified transition
	 * 
	 * @param fac
	 * @param key
	 * @param target
	 * @throws TransitionException 
	 */
	public <K,T> void checkRedirectToTransition(TransitionFactory<K,T> fac, K key, T target) throws TransitionException{
		checkRedirect(TransitionServlet.getURL(getContext(), fac, target,key));
	}
	/** Check that the response has been redirected to the supplied url
	 * 
	 * @param url URL to check
	 */
	public void checkRedirect(String url){
		assertEquals(HttpServletResponse.SC_OK,res.error);
		assertNull(res.error_str);
		assertNull("Forward not redirect",res.forward);;
		assertEquals("Wrong redirect", TEST_APP_NAME+url, res.redirect);
	}
	/** Check that the response has been forwarded to the supplied url.
	 * 
	 * @param url
	 */
	public void checkForward(String url){
		assertEquals(HttpServletResponse.SC_OK,res.error);
		assertNull(res.error_str);
		assertNull("redirect not forward ",res.redirect);
		assertEquals("Wrong redirect", url, res.forward);
	}
	
	
	
	/** Check that the specified confirm message has been requested
	 * then reset the response and modify the request with the specified confirmation
	 * 
	 * @param type
	 */
	public void runConfirm(String type, boolean response){
		ResourceBundle mess = ResourceBundle.getBundle("confirm");
		
		assertNotNull(mess.getString(type+".title"));
		assertNotNull(mess.getString(type+".text"));
		assertEquals(type, req.getAttribute(WebappServlet.CONFIRM_TYPE));
		checkForward(WebappServlet.SCRIPTS_CONFIRM_JSP);
		res = new MockResponse();
		ctx.setService(new DefaultServletService(ctx, serv_ctx, req, res));
		if( response){
			addParam(WebappServlet.CONFIRM_YES, "anything");
		}else{
			addParam(WebappServlet.CONFIRM_NO, "anything");
		}
	}
	/** Check that the response has forwarded to the messages page to show the
	 * specified messate
	 * 
	 * @param message
	 */
	public void checkMessage(String message){
		checkForward("/messages.jsp");
		assertEquals(message, req.getAttribute("message_type"));
	}
/** Add a form parameter to the request.
 * 
 * @param name
 * @param value
 */
	public void addParam(String name, String value) {
		req.params.put(name, value);
	}
	
	public void addParam(String name, int value){
		req.params.put(name,Integer.toString(value));
	}
	public void addParam(String name, MimeStreamData value) {
		req.params.put(name, value);
	}

	/** Add a {@link Indexed} object as a form parameter using the
	 * default encoding (the object id).
	 * 
	 * @param name
	 * @param i
	 */
	public void addParam(String name,Indexed i){
		addParam(name, Integer.toString(i.getID()));
	}
	/** Set the form action. This only needs to be called when there is more than one action specified 
	 * 
	 * @param action The action String specifed in the {@link Form}
	 */
	public void setAction(String action) {
		req.params.put(action, action);
	}
	
	protected final void doPost() throws ServletException, IOException{
		servlet.doPost(req, res);
	}
	
/** ensure that all registered classifier tables have been created
 * 
 */
	public void bootstrapClassifiers() {
		AppContext context = getContext();
		Map<String,Class> classifiers = context.getClassMap(ClassificationFactory.class);
		for(String tag : classifiers.keySet()){
			ClassificationFactory fac = context.makeObject(ClassificationFactory.class, tag);
		}
	}

	/** Lookup a user by email and install them as the current person in the session.
	 * @param email
	 * @return
	 * @throws DataException
	 */
	protected SessionService setupPerson(String email) throws DataException {
		AppContext context = getContext();
		SessionService sess = context.getService(SessionService.class);
		AppUserFactory<?> fac = sess.getLoginFactory();
		AbstractSessionService.setupRoleTable(context);
		AppUser person = fac.findByEmail(email,true);
		if( person != null ){
			sess.setCurrentPerson(person);
		}else{
			// Try to make a scratch user
			person = (AppUser) fac.makeBDO();
			person.setEmail(email);
			person.commit();
			sess.setCurrentPerson(person);
		}
		return  sess;
	}
	
}