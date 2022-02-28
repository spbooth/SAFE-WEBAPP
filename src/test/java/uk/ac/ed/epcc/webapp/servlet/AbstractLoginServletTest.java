package uk.ac.ed.epcc.webapp.servlet;

import java.io.IOException;

import javax.servlet.ServletException;

import uk.ac.ed.epcc.webapp.mock.MockServletConfig;
import uk.ac.ed.epcc.webapp.session.AppUser;

public abstract class AbstractLoginServletTest<A extends AppUser> extends ServletTest {

	public AbstractLoginServletTest() {
		super();
	}

	/**
	 * @throws IOException 
	 * @throws ServletException 
	 * 
	 */
	public void loginRedirects() throws ServletException, IOException {
		loginRedirects("/main.jsp");
	}

	public void loginRedirects(String expected) throws ServletException, IOException {
		if( LoginServlet.COOKIE_TEST.isEnabled(ctx)) {
			checkRedirect("/LoginServlet");
			doPost();
		}
		checkRedirect(expected);
	}

	@Override
	public void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		servlet=new LoginServlet<A>();
		MockServletConfig config = new MockServletConfig(serv_ctx, "LoginServlet");
		servlet.init(config);
		req.servlet_path="LoginServlet";
		
	}

}