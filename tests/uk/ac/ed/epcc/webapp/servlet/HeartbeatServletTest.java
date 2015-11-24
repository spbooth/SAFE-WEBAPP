package uk.ac.ed.epcc.webapp.servlet;

import javax.servlet.ServletException;

import org.junit.Before;

import uk.ac.ed.epcc.webapp.mock.MockServletConfig;
import uk.ac.ed.epcc.webapp.servlet.HeartbeatServlet;
import uk.ac.ed.epcc.webapp.servlet.session.ServletSessionService;

public abstract class HeartbeatServletTest extends ServletTest {

	public HeartbeatServletTest() {
		super();
	}
	
	public abstract ServletSessionService makeService();

	@Before
	public void setServlet() throws ServletException {
		servlet = new HeartbeatServlet();
		MockServletConfig config = new MockServletConfig(serv_ctx, "HeartbeatServlet");
		servlet.init(config);
		getContext().setService(makeService());
	}

}