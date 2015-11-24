// Copyright - The University of Edinburgh 2013
package uk.ac.ed.epcc.webapp.mock;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.1 $")
public class MockRequestDispatcher implements RequestDispatcher {

	String url;
	/**
	 * 
	 */
	public MockRequestDispatcher(String url) {
		this.url=url;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.RequestDispatcher#forward(javax.servlet.ServletRequest, javax.servlet.ServletResponse)
	 */
	public void forward(ServletRequest arg0, ServletResponse arg1)
			throws ServletException, IOException {
		MockResponse res = (MockResponse) arg1;
		res.forward=url;

	}

	/* (non-Javadoc)
	 * @see javax.servlet.RequestDispatcher#include(javax.servlet.ServletRequest, javax.servlet.ServletResponse)
	 */
	public void include(ServletRequest arg0, ServletResponse arg1)
			throws ServletException, IOException {

	}

}
