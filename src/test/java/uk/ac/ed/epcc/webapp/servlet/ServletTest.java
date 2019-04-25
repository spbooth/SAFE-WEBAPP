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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.junit.After;
import org.junit.Before;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Indexed;
import uk.ac.ed.epcc.webapp.TestDataHelper;
import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.config.ConfigService;
import uk.ac.ed.epcc.webapp.config.OverrideConfigService;
import uk.ac.ed.epcc.webapp.content.HtmlBuilder;
import uk.ac.ed.epcc.webapp.content.HtmlContentFormat;
import uk.ac.ed.epcc.webapp.content.PreDefinedContent;
import uk.ac.ed.epcc.webapp.content.SimpleXMLBuilder;
import uk.ac.ed.epcc.webapp.forms.Form;
import uk.ac.ed.epcc.webapp.forms.SetParamVisitor;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
import uk.ac.ed.epcc.webapp.forms.exceptions.TransitionException;
import uk.ac.ed.epcc.webapp.forms.html.HTMLForm;
import uk.ac.ed.epcc.webapp.forms.html.RedirectResult;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.result.ChainedTransitionResult;
import uk.ac.ed.epcc.webapp.forms.result.CustomPage;
import uk.ac.ed.epcc.webapp.forms.result.FormResult;
import uk.ac.ed.epcc.webapp.forms.transition.BaseFormTransition;
import uk.ac.ed.epcc.webapp.forms.transition.TargetLessTransition;
import uk.ac.ed.epcc.webapp.forms.transition.Transition;
import uk.ac.ed.epcc.webapp.forms.transition.TransitionFactory;
import uk.ac.ed.epcc.webapp.forms.transition.ViewTransitionFactory;
import uk.ac.ed.epcc.webapp.jdbc.config.DataBaseConfigService;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.logging.debug.DebugLoggerService;
import uk.ac.ed.epcc.webapp.logging.print.PrintLoggerService;
import uk.ac.ed.epcc.webapp.messages.MessageBundleService;
import uk.ac.ed.epcc.webapp.mock.MockPart;
import uk.ac.ed.epcc.webapp.mock.MockRequest;
import uk.ac.ed.epcc.webapp.mock.MockResponse;
import uk.ac.ed.epcc.webapp.mock.MockServletContext;
import uk.ac.ed.epcc.webapp.mock.MockSession;
import uk.ac.ed.epcc.webapp.model.ClassificationFactory;
import uk.ac.ed.epcc.webapp.model.data.XMLDataUtils;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.stream.ByteArrayMimeStreamData;
import uk.ac.ed.epcc.webapp.model.data.stream.MimeStreamData;
import uk.ac.ed.epcc.webapp.servlet.session.ServletSessionService;
import uk.ac.ed.epcc.webapp.session.AbstractSessionService;
import uk.ac.ed.epcc.webapp.session.AppUser;
import uk.ac.ed.epcc.webapp.session.AppUserFactory;
import uk.ac.ed.epcc.webapp.session.SessionService;
import uk.ac.ed.epcc.webapp.session.SimpleSessionService;
import uk.ac.ed.epcc.webapp.timer.DefaultTimerService;
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
 * @see AbstractTransitionServletTest
 * @author spb
 *
 */


public abstract class ServletTest extends WebappTestBase{
	/** A request to the servlet run from a seperate thread.
	 * On creation the current request state is moved to this object and the main request reset.
	 * A new AppContext is created for the new request 
	 * 
	 * @author spb
	 *
	 */
	public class BrowserRunnable implements Runnable{
		

		public MockRequest req=null;
		public MockResponse res=null;
		public AppContext ctx;
		public Exception e=null;
		
		public BrowserRunnable() throws SQLException{
			this.req = ServletTest.this.req;
			this.res = ServletTest.this.res;
			this.ctx = duplicate(ServletTest.this.ctx);
			ctx.setService(new Servlet3MultiPartServletService(ctx, serv_ctx, req, res));
			this.req.setAttribute(ErrorFilter.APP_CONTEXT_ATTR, this.ctx); // we will retreive appcontext from request
			ServletTest.this.resetRequest();
			
		}
		private AppContext duplicate(AppContext orig) throws SQLException{
			AppContext dup = new AppContext();
			dup.setService(new PrintLoggerService());
			dup.setService(new DebugLoggerService(dup));
			SimpleSessionService service = new SimpleSessionService(dup);
			dup.setService( service);
			service.setCurrentPerson(orig.getService(SessionService.class).getCurrentPerson().getID());
			dup.setService(new DataBaseConfigService(dup));
			ConfigService serv = orig.getService(ConfigService.class);
			if( serv instanceof OverrideConfigService){
				dup.setService(new OverrideConfigService(((OverrideConfigService)serv).getOverrides(), dup));
			}
			DefaultTimerService timer_service = new DefaultTimerService(dup);
			timer_service.setPrefix("BrowserRunnable ");
			dup.setService(timer_service);
			
			return dup;
		}
		
		/** Copy the request/response back to the test to be queried.
		 * 
		 */
		public void restore(){
			ServletTest.this.req=req;
			ServletTest.this.res=res;
			ServletTest.this.req.setAttribute(ErrorFilter.APP_CONTEXT_ATTR, ServletTest.this.ctx);
		}

		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			try {
				servlet.doPost(req, res);
				System.out.println("Browser thread finished");
			} catch (Exception e) {
				e.printStackTrace(System.err);
				this.e=e;
			}
			
		}
	}
	
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
		String remote_user = null;
		if( req != null){
			session=req.session;
			remote_user=req.remote_user;
		}
		res = new MockResponse();
		req= new MockRequest(TEST_APP_NAME);
		req.session=session;
		req.remote_user=remote_user;
	
		
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
	 * 
	 * 
	 * @param url URL to check
	 */
	public void checkRedirect(String url){
		assertEquals(HttpServletResponse.SC_OK,res.error);
		assertNull(res.error_str);
		assertNull("Forward not redirect",res.forward);
		if( url.startsWith("http")){
			assertEquals("Wrong redirect", url, res.redirect);
		}else{
			assertEquals("Wrong redirect", TEST_APP_NAME+url, res.redirect);
		}
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
	
	public void checkRequestAuth(String page) {
		HttpSession session = req.getSession(false);
		assertNotNull(session);
		RedirectResult result = (RedirectResult) session.getAttribute(LoginServlet.INITIAL_PAGE_ATTR);
		assertNotNull(result);
		assertEquals(page, result.getURL());
		checkForward(LoginServlet.getLoginPage(ctx));
	}
	/** Assert form error reported with a specific error reported on one of the parameters
	 * 
	 * @param expected_url  form-url redirected to.
	 * @param param parameter with error (null to skip check)
	 * @param error expected error text.
	 */
	public void checkError(String expected_url,String param, String error){
		checkForward(expected_url);
		Map<String,String> errors = HTMLForm.getErrors(req);
		//assertTrue(errors.containsKey(param));
		if( param != null) {
			assertEquals(error, errors.get(param));
		}
	}
	/** Check that the specified confirm message has been requested
	 * then reset the response and modify the request with the specified confirmation
	 * 
	 * @param type
	 */
	public void runConfirm(String type, boolean response){
		ResourceBundle mess = getContext().getService(MessageBundleService.class).getBundle("confirm");
		
		assertNotNull(mess.getString(type+".title"));
		assertNotNull(mess.getString(type+".text"));
		assertEquals("Confirm type",type, req.getAttribute(WebappServlet.CONFIRM_TYPE));
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
	 * specified message
	 * 
	 * @param message
	 */
	public void checkMessage(String message){
		checkForward("/messages.jsp");
		assertEquals(message, req.getAttribute("message_type"));
	}
	
	public void checkMessageText( String expected) {
		String message_type = (String) req.getAttribute("message_type");
		Object args[] = (Object[]) req.getAttribute("args");
		if(args == null) args = new Object[0];
		
		ResourceBundle mess = getContext().getService(MessageBundleService.class).getBundle();
		PreDefinedContent text = new PreDefinedContent(ctx,mess,message_type + ".text",args);
		HtmlBuilder buffer = new HtmlBuilder();
		text.addContent((SimpleXMLBuilder)buffer);
		assertEquals(expected, buffer.toString());
	}
	
	public void checkTransitionException(String message){
		checkForward("/messages.jsp");
		assertEquals("transition_error", req.getAttribute("message_type"));
		Object args[] = (Object[]) req.getAttribute("args");
		assertNotNull(args);
		assertEquals(message,args[1]);
	}
/** Add a form parameter to the request.
 * 
 * @param name
 * @param value
 */
	public void addParam(String name, String value) {
		req.removeAttribute(DefaultServletService.PARAMS_KEY_NAME);
		req.params.put(name, value);
		req.method="POST";
	}
	/** Add the form parameters corresponding to an Input.
	 * The Input key value must be set to the base parameter name
	 * 
	 * Useful for setting multi-input parameters
	 * 
	 * @param i {@link Input}
	 * @throws Exception 
	 */
	public void addParam(Input i) throws Exception {
		SetParamVisitor vis = new SetParamVisitor(req.params);
		req.removeAttribute(DefaultServletService.PARAMS_KEY_NAME);
		i.accept(vis);
		req.method="POST";

	}
	public void clearParam(String name) {
		req.removeAttribute(DefaultServletService.PARAMS_KEY_NAME);
		req.params.remove(name);
	}
	public void addParam(String name, int value){
		req.removeAttribute(DefaultServletService.PARAMS_KEY_NAME);
		req.params.put(name,Integer.toString(value));
	}
	

	public void addUploadParam(String name, String mime_type, String resource_name) throws DataFault, IOException {
		String file_name = getContext().expandText(resource_name);
		MockPart part = new MockPart(name);
		part.setFileName(file_name);
		part.data.setMimeType(mime_type);
		part.data.read(getClass().getResourceAsStream(file_name));
		req.addPart(part);
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
	/** Set the form action. This only needs to be called when there is more than one action (submit button) specified 
	 * 
	 * @param action The action String specified in the {@link Form}
	 */
	public void setAction(String action) {
		req.params.put(action.trim(), action.trim());
		req.method="POST";
	}
	
	protected final void doPost() throws ServletException, IOException{
		servlet.doPost(req, res);
		deferredEmails();

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
			try {
				person = fac.makeFromString(email);
			} catch (ParseException e) {
				
			}
			if( person == null){
				person = (AppUser) fac.makeBDO();
				person.setEmail(email);
			}
			person.commit();
			sess.setCurrentPerson(person);
		}
		return  sess;
	}
	/** Generates a XML (mostly HTML) representation of the 
	 * contents of the view_custompage that can be directly 
	 * influenced by the transform. This is then (optionally) put through a
	 * normalisation XLST transform to remove time dependent 
	 * output and compared with a file of expected output.
	 * 
	 * 
	 * @param normalise_transform
	 * @param expected

	 * @throws Exception 
	 */
	public void checkViewCustomPage(String normalize_transform, String expected_xml) throws Exception{
		checkForward("/scripts/view_custom_page.jsp");
		CustomPage custom_page =(CustomPage) req.getAttribute(CustomPage.CUSTOM_PAGE_TAG);
		assertNotNull(custom_page);
		HtmlBuilder builder = new HtmlBuilder();
		builder.setValidXML(true);
		builder.open("view_page");
		custom_page.addContent(getContext(), builder);
		
		builder.close();
		 
		String content = builder.toString();
		 checkContent(normalize_transform, expected_xml, content);
		
	}

	/** Process a {@link FormResult} directly
	 * 
	 * This is to simulate a {@link FormResult} handled in a jsp.
	 * use {@link #doPost()} to simulate posts to servlets.
	 * 
	 * @param result
	 * @throws Exception
	 */
    public void doFormResult(FormResult result) throws Exception{
    	ServletFormResultVisitor vis = new ServletFormResultVisitor(ErrorFilter.retrieveAppContext(req, res), req, res);
		result.accept(vis);
    }
	/** Check transition attributes.
	 * 
	 * We need this in servlet test as servlets may generate a chain result to a transitions
	 * 
	 * @param provider
	 * @param key
	 * @param target
	 */
    public <K, T> void checkAttributes(TransitionFactory<K, T> provider, K key,
			T target) {
		assertNotNull("null provider",provider);
		TransitionFactory transitionFactory = (TransitionFactory)req.getAttribute(TransitionServlet.TRANSITION_PROVIDER_ATTR);
		assertNotNull("null factory",transitionFactory);
		assertEquals(provider.getTargetName(), transitionFactory.getTargetName());
		if( key == null ){
			assertNull(req.getAttribute(TransitionServlet.TRANSITION_KEY_ATTR));
		}else{
			assertEquals("key/attribute mis-match",key, req.getAttribute(TransitionServlet.TRANSITION_KEY_ATTR));
		}
		if( target == null ){
			assertNull(req.getAttribute(TransitionServlet.TARGET_ATTRIBUTE));
		}else{
			assertEquals("target/attribute mis-match",target, req.getAttribute(TransitionServlet.TARGET_ATTRIBUTE));
		}
	}
    
    /** check for a non-bookmarkable chained transition.
	 * Note this should always be a form transition.
	 * 
	 * These are normally used where one servlet of form transition selects the target for a. If the first transition
	 * changes state then the transition should really have used a redirect.
	 * 
	 * @see ChainedTransitionResult#useURL()
	 * @param fac
	 * @param key
	 * @param target
	 * @throws TransitionException
	 */
	public <K, T> void checkForwardToTransition(TransitionFactory<K, T> fac,
			K key, T target) throws TransitionException {
		checkAttributes(fac, key, target);
		Transition t = fac.getTransition(target, key);
		assertNotNull(t);
		assertTrue( t instanceof BaseFormTransition || t instanceof TargetLessTransition);
		assertEquals(t instanceof TargetLessTransition, target == null);
		
		checkForward("/scripts/transition.jsp");

	}
	
	/** check that the result is consistent with a forward to the view_target page. 
	 * Normally this should not be needed in tests as most operations should be written 
	 * to use a redirect to the canonical object URL to generate the view page.
	 * You could call this after {@link #checkViewRedirect(ViewTransitionFactory, Object)}
	 * to verify the servlet does the correct thing with a view url but this would be testing the servlet not
	 * the transition class.
	 * @param provider
	 * @param target
	 */
	public <K,T> void checkViewForward(ViewTransitionFactory<K, T> provider, T target){
		checkAttributes(provider, null, target);
		checkForward("/scripts/view_target.jsp");
	}
	
	/** This is the normal view result. These should always redirect to the canonical url to
	 * generate a view page
	 * @param provider
	 * @param target
	 * @throws TransitionException 
	 */
	public <K,T> void checkViewRedirect(ViewTransitionFactory<K, T> provider, T target) throws TransitionException{
		checkRedirectToTransition(provider, null, target);
		assertTrue("redirect to forbidden view",provider.canView(target, getContext().getService(SessionService.class)));
	}
}