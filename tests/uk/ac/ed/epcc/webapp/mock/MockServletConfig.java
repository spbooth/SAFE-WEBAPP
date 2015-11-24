// Copyright - The University of Edinburgh 2014
package uk.ac.ed.epcc.webapp.mock;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

/**
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.1 $")
public class MockServletConfig implements ServletConfig {

	private final String name;
	private final ServletContext context;
	private Hashtable<String,String> params = new Hashtable<String, String>();
	/**
	 * 
	 */
	public MockServletConfig(ServletContext context,String name) {
		this.context=context;
		this.name=name;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletConfig#getInitParameter(java.lang.String)
	 */
	public String getInitParameter(String arg0) {
		return params.get(arg0);
	}

	public void setInitParameter(String key,String value){
		params.put(key, value);
	}
	/* (non-Javadoc)
	 * @see javax.servlet.ServletConfig#getInitParameterNames()
	 */
	public Enumeration getInitParameterNames() {
		return params.keys();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletConfig#getServletContext()
	 */
	public ServletContext getServletContext() {
		return context;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletConfig#getServletName()
	 */
	public String getServletName() {
		return name;
	}

}
