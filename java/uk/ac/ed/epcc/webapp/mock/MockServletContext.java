//| Copyright - The University of Edinburgh 2015                            |
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
	Hashtable<String,Object> attr = new Hashtable<>();
	Properties prop = new Properties();
	@Override
	public Object getAttribute(String arg0) {
		return attr.get(arg0);
	}

	@Override
	public Enumeration getAttributeNames() {
	
		return attr.keys();
	}

	@Override
	public ServletContext getContext(String arg0) {
		
		return null;
	}

	@Override
	public String getInitParameter(String arg0) {
		return prop.getProperty(arg0);
	}

	@Override
	public Enumeration getInitParameterNames() {
		return prop.keys();
	}

	public void setProps(Properties prop){
		this.prop=prop;
	}
	public void addProp(String name,String value) {
		if( prop == null ) {
			prop=new Properties();
		}
		prop.setProperty(name, value);
	}
	@Override
	public int getMajorVersion() {
		
		return 0;
	}

	@Override
	public String getMimeType(String arg0) {
		
		return null;
	}

	@Override
	public int getMinorVersion() {
		
		return 0;
	}

	@Override
	public RequestDispatcher getNamedDispatcher(String arg0) {
		
		return null;
	}

	@Override
	public String getRealPath(String arg0) {
		return arg0;
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String arg0) {
		
		return new MockRequestDispatcher(arg0);
	}

	@Override
	public URL getResource(String arg0) throws MalformedURLException {
		
		return null;
	}

	@Override
	public InputStream getResourceAsStream(String arg0) {
		
		return null;
	}

	@Override
	public Set getResourcePaths(String arg0) {
		
		return null;
	}

	@Override
	public String getServerInfo() {
		
		return null;
	}

	
	@Override
	public Servlet getServlet(String arg0) throws ServletException {
		
		return null;
	}

	@Override
	public String getServletContextName() {
		
		return null;
	}

	
	@Override
	public Enumeration getServletNames() {
		
		return null;
	}

	
	@Override
	public Enumeration getServlets() {
		
		return null;
	}

	@Override
	public void log(String arg0) {
		
		System.err.println("MockServletContext log "+arg0);
	}


	@Override
	public void log(Exception arg0, String arg1) {
		
		System.err.println("MockServletContext log "+arg1);
		arg0.printStackTrace();
	}

	@Override
	public void log(String arg0, Throwable arg1) {
		System.err.println("MockServletContext log "+arg0);
		arg1.printStackTrace();

	}

	@Override
	public void removeAttribute(String arg0) {
		attr.remove(arg0);

	}

	@Override
	public void setAttribute(String arg0, Object arg1) {
		attr.put(arg0,arg1);

	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#addFilter(java.lang.String, java.lang.String)
	 */
	
	@Override
	public Dynamic addFilter(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#addFilter(java.lang.String, javax.servlet.Filter)
	 */
	@Override
	public Dynamic addFilter(String arg0, Filter arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#addFilter(java.lang.String, java.lang.Class)
	 */
	@Override
	public Dynamic addFilter(String arg0, Class<? extends Filter> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#addListener(java.lang.Class)
	 */
	@Override
	public void addListener(Class<? extends EventListener> arg0) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#addListener(java.lang.String)
	 */
	@Override
	public void addListener(String arg0) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#addListener(java.util.EventListener)
	 */
	@Override
	public <T extends EventListener> void addListener(T arg0) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#addServlet(java.lang.String, java.lang.String)
	 */
	@Override
	public javax.servlet.ServletRegistration.Dynamic addServlet(String arg0,
			String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#addServlet(java.lang.String, javax.servlet.Servlet)
	 */
	
	@Override
	public javax.servlet.ServletRegistration.Dynamic addServlet(String arg0,
			Servlet arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#addServlet(java.lang.String, java.lang.Class)
	 */
	
	@Override
	public javax.servlet.ServletRegistration.Dynamic addServlet(String arg0,
			Class<? extends Servlet> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#createFilter(java.lang.Class)
	 */
	
	@Override
	public <T extends Filter> T createFilter(Class<T> arg0)
			throws ServletException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#createListener(java.lang.Class)
	 */
	
	@Override
	public <T extends EventListener> T createListener(Class<T> arg0)
			throws ServletException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#createServlet(java.lang.Class)
	 */
	
	@Override
	public <T extends Servlet> T createServlet(Class<T> arg0)
			throws ServletException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#declareRoles(java.lang.String[])
	 */
	
	@Override
	public void declareRoles(String... arg0) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getClassLoader()
	 */
	
	@Override
	public ClassLoader getClassLoader() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getContextPath()
	 */
	
	@Override
	public String getContextPath() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getDefaultSessionTrackingModes()
	 */
	
	@Override
	public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getEffectiveMajorVersion()
	 */
	
	@Override
	public int getEffectiveMajorVersion() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getEffectiveMinorVersion()
	 */
	
	@Override
	public int getEffectiveMinorVersion() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getEffectiveSessionTrackingModes()
	 */
	
	@Override
	public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getFilterRegistration(java.lang.String)
	 */
	
	@Override
	public FilterRegistration getFilterRegistration(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getFilterRegistrations()
	 */
	
	@Override
	public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getJspConfigDescriptor()
	 */
	
	@Override
	public JspConfigDescriptor getJspConfigDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getServletRegistration(java.lang.String)
	 */
	
	@Override
	public ServletRegistration getServletRegistration(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getServletRegistrations()
	 */
	
	@Override
	public Map<String, ? extends ServletRegistration> getServletRegistrations() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getSessionCookieConfig()
	 */
	
	@Override
	public SessionCookieConfig getSessionCookieConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#setInitParameter(java.lang.String, java.lang.String)
	 */
	
	@Override
	public boolean setInitParameter(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#setSessionTrackingModes(java.util.Set)
	 */
	
	@Override
	public void setSessionTrackingModes(Set<SessionTrackingMode> arg0)
			throws IllegalStateException, IllegalArgumentException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getVirtualServerName()
	 */
	
	@Override
	public String getVirtualServerName() {
		// TODO Auto-generated method stub
		return null;
	}

}