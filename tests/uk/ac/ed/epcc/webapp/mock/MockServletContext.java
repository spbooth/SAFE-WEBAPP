/*******************************************************************************
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.mock;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.FilterRegistration.Dynamic;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import javax.servlet.descriptor.JspConfigDescriptor;

public class MockServletContext implements ServletContext {
	Hashtable<String,Object> attr = new Hashtable<String,Object>();
	Properties prop = new Properties();
	public Object getAttribute(String arg0) {
		return attr.get(arg0);
	}

	public Enumeration getAttributeNames() {
	
		return attr.keys();
	}

	public ServletContext getContext(String arg0) {
		
		return null;
	}

	public String getInitParameter(String arg0) {
		return prop.getProperty(arg0);
	}

	public Enumeration getInitParameterNames() {
		return prop.keys();
	}

	public void setProps(Properties prop){
		this.prop=prop;
	}
	public int getMajorVersion() {
		
		return 0;
	}

	public String getMimeType(String arg0) {
		
		return null;
	}

	public int getMinorVersion() {
		
		return 0;
	}

	public RequestDispatcher getNamedDispatcher(String arg0) {
		
		return null;
	}

	public String getRealPath(String arg0) {
		return arg0;
	}

	public RequestDispatcher getRequestDispatcher(String arg0) {
		
		return new MockRequestDispatcher(arg0);
	}

	public URL getResource(String arg0) throws MalformedURLException {
		
		return null;
	}

	public InputStream getResourceAsStream(String arg0) {
		
		return null;
	}

	public Set getResourcePaths(String arg0) {
		
		return null;
	}

	public String getServerInfo() {
		
		return null;
	}

	
	public Servlet getServlet(String arg0) throws ServletException {
		
		return null;
	}

	public String getServletContextName() {
		
		return null;
	}

	
	public Enumeration getServletNames() {
		
		return null;
	}

	
	public Enumeration getServlets() {
		
		return null;
	}

	public void log(String arg0) {
		

	}


	public void log(Exception arg0, String arg1) {
		

	}

	public void log(String arg0, Throwable arg1) {
		

	}

	public void removeAttribute(String arg0) {
		attr.remove(arg0);

	}

	public void setAttribute(String arg0, Object arg1) {
		attr.put(arg0,arg1);

	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#addFilter(java.lang.String, java.lang.String)
	 */
	
	public Dynamic addFilter(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#addFilter(java.lang.String, javax.servlet.Filter)
	 */
	public Dynamic addFilter(String arg0, Filter arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#addFilter(java.lang.String, java.lang.Class)
	 */
	public Dynamic addFilter(String arg0, Class<? extends Filter> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#addListener(java.lang.Class)
	 */
	public void addListener(Class<? extends EventListener> arg0) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#addListener(java.lang.String)
	 */
	public void addListener(String arg0) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#addListener(java.util.EventListener)
	 */
	public <T extends EventListener> void addListener(T arg0) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#addServlet(java.lang.String, java.lang.String)
	 */
	public javax.servlet.ServletRegistration.Dynamic addServlet(String arg0,
			String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#addServlet(java.lang.String, javax.servlet.Servlet)
	 */
	
	public javax.servlet.ServletRegistration.Dynamic addServlet(String arg0,
			Servlet arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#addServlet(java.lang.String, java.lang.Class)
	 */
	
	public javax.servlet.ServletRegistration.Dynamic addServlet(String arg0,
			Class<? extends Servlet> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#createFilter(java.lang.Class)
	 */
	
	public <T extends Filter> T createFilter(Class<T> arg0)
			throws ServletException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#createListener(java.lang.Class)
	 */
	
	public <T extends EventListener> T createListener(Class<T> arg0)
			throws ServletException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#createServlet(java.lang.Class)
	 */
	
	public <T extends Servlet> T createServlet(Class<T> arg0)
			throws ServletException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#declareRoles(java.lang.String[])
	 */
	
	public void declareRoles(String... arg0) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getClassLoader()
	 */
	
	public ClassLoader getClassLoader() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getContextPath()
	 */
	
	public String getContextPath() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getDefaultSessionTrackingModes()
	 */
	
	public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getEffectiveMajorVersion()
	 */
	
	public int getEffectiveMajorVersion() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getEffectiveMinorVersion()
	 */
	
	public int getEffectiveMinorVersion() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getEffectiveSessionTrackingModes()
	 */
	
	public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getFilterRegistration(java.lang.String)
	 */
	
	public FilterRegistration getFilterRegistration(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getFilterRegistrations()
	 */
	
	public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getJspConfigDescriptor()
	 */
	
	public JspConfigDescriptor getJspConfigDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getServletRegistration(java.lang.String)
	 */
	
	public ServletRegistration getServletRegistration(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getServletRegistrations()
	 */
	
	public Map<String, ? extends ServletRegistration> getServletRegistrations() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getSessionCookieConfig()
	 */
	
	public SessionCookieConfig getSessionCookieConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#setInitParameter(java.lang.String, java.lang.String)
	 */
	
	public boolean setInitParameter(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#setSessionTrackingModes(java.util.Set)
	 */
	
	public void setSessionTrackingModes(Set<SessionTrackingMode> arg0)
			throws IllegalStateException, IllegalArgumentException {
		// TODO Auto-generated method stub
		
	}

}
