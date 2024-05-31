package uk.ac.ed.epcc.webapp.servlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.email.Emailer;
import uk.ac.ed.epcc.webapp.email.MockTansport;
import uk.ac.ed.epcc.webapp.mock.MockFilterConfig;
import uk.ac.ed.epcc.webapp.mock.MockRequest;
import uk.ac.ed.epcc.webapp.mock.MockResponse;
import uk.ac.ed.epcc.webapp.mock.MockServletContext;

public class ErrorFilterTest {

	public MockServletContext ctx;
	@Before
	public void setup() throws IOException {
		ctx = new MockServletContext();
		Properties props = new Properties();
		props.load(getClass().getResourceAsStream("errorfilter.properties"));
		ctx.setProps(props);
	}
	@Test
	public void testMakeContext() throws Exception {
		
		
		AppContext conn = ErrorFilter.makeContext(ctx);
		
		String value = conn.getInitParameter("test.value");
		assertEquals("hello", value);
		AppContext.clearContext();
		conn.close();
	}
	
	@Test
	public void testRetreiveContext() throws ServletException, MessagingException {
		MockTansport.clear();
		Emailer.resetReport();
		MockRequest req = new MockRequest("/test");
		MockResponse res = new MockResponse();
		req.setAttribute(ErrorFilter.SERVLET_CONTEXT_ATTR, ctx);
		AppContext conn = ErrorFilter.retrieveAppContext(req, res);
		
		assertNotNull(conn);
		String value = conn.getInitParameter("test.value");
		assertEquals("hello", value);
		int count = MockTansport.nSent();
		assertTrue("emails sent" ,count > 0);
		Message first = MockTansport.getMessage(0);
		String subject = first.getSubject();
		assertEquals("test Error Report Bad connection pool", subject);
		
	}
	
	@Test
	public void doFilterTest() throws ServletException, IOException {
		ErrorFilter fil = new ErrorFilter();
		fil.init(new MockFilterConfig(ctx, "ErrorFilter"));
		MockRequest req = new MockRequest("/test");
		MockResponse res = new MockResponse();
		fil.doFilter(req, res, new FilterChain() {
			
			@Override
			public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
				res.getOutputStream().print(ErrorFilter.retrieveAppContext(req, res).getInitParameter("test.value"));
				
			}
		});
		
		String value = res.getOutputStream().toString();
		assertEquals("hello", value);
	}
	
	@Test
	public void doFilterErrorTest() throws ServletException, IOException {
		MockTansport.clear();
		ErrorFilter fil = new ErrorFilter();
		// error emails never defferred
		//ctx.addProp("service.feature.email.deferred_send", "on");
		Emailer.resetReport();
		fil.init(new MockFilterConfig(ctx, "ErrorFilter"));
		MockRequest req = new MockRequest("/test");
		MockResponse res = new MockResponse();
		fil.doFilter(req, res, new FilterChain() {
			
			@Override
			public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
					// make appcontext so proper logger used
				    AppContext conn = ErrorFilter.retrieveAppContext(req, res);
				
					throw new ServletException(new Exception("A test error"));
				
			}
		});
		
		assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, res.getStatus());
		assertTrue("Emails sent", MockTansport.nSent()>0);
	}
	
}
