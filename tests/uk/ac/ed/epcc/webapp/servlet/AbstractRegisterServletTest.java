// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.servlet;

import javax.servlet.ServletException;

import org.junit.Before;

import uk.ac.ed.epcc.webapp.forms.MapForm;
import uk.ac.ed.epcc.webapp.forms.factory.FormCreator;
import uk.ac.ed.epcc.webapp.mock.MockServletConfig;
import uk.ac.ed.epcc.webapp.session.AppUserFactory;
import uk.ac.ed.epcc.webapp.session.SessionService;

/**
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.4 $")
public abstract class AbstractRegisterServletTest extends ServletTest {

	
	@Before
	public void setConfig() throws ServletException{
		servlet=makeServlet();
		MockServletConfig config = new MockServletConfig(serv_ctx, "RegisterServlet");
		servlet.init(config);
	}

	/**
	 * @return
	 */
	protected WebappServlet makeServlet() {
		return new RegisterServlet();
	}
	
	
	
	public void prepareSignup() throws Exception{
		
		req.servlet_path="/SignupServlet";
		req.params.put("form_url", "/scripts/signup.jsp");
		AppUserFactory person_fac =  getContext().getService(SessionService.class).getLoginFactory();
	     //PasswordAuthAppUserFactory<?> person_fac = new PasswordPersonFactory(conn,"Person");
		MapForm f = new MapForm(getContext());
	    FormCreator signupFormCreator = person_fac.getSignupFormCreator(null);
	    signupFormCreator.buildCreationForm("Signup", f);
	    f.addStringMap(req.params);
	}
}
