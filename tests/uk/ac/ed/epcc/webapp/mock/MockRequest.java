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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;

import java.util.Hashtable;

public class MockRequest implements HttpServletRequest {
    public Hashtable<String,Object> params=new Hashtable<String,Object>();
    public HashMap<String,Object> attr = new HashMap<String,Object>();
    public HashMap<String,String> header = new HashMap<String,String>();
    public Set<String> roles = new HashSet<String>();
    public Map<String,Part> parts = new HashMap<String,Part>();
    public String context_path;
    public String servlet_path;
    public String path_info="";
    public String method="POST";
    public MockSession session;
    public String remote_user=null;
    public String content_type=null;
    public Principal principal=null;
    public MockRequest(String path){
    	context_path=path;
    }
	public String getAuthType() {
		
		return null;
	}

	public String getContextPath() {
		return context_path;
	}
	public void setContextPath(String path){
		context_path=path;
	}

	public Cookie cookies[] = null;
	public Cookie[] getCookies() {
		
		return cookies;
	}

	public long getDateHeader(String arg0) {
		
		return 0;
	}

	public String getHeader(String arg0) {
		
		return header.get(arg0);
	}

	public Enumeration getHeaderNames() {
		
		return Collections.enumeration(header.keySet());
	}

	public Enumeration getHeaders(String arg0) {
		
		return null;
	}

	public int getIntHeader(String arg0) {
		
		return 0;
	}

	public String getMethod() {
		
		return method;
	}

	public String getPathInfo() {
		
		return path_info;
	}

	public String getPathTranslated() {
		
		return null;
	}

	public String getQueryString() {
		
		return null;
	}

	public String getRemoteUser() {
		
		return remote_user;
	}

	public String getRequestURI() {
		
		return getServletPath()+"/"+getPathInfo();
	}

	public StringBuffer getRequestURL() {
		
		return null;
	}

	public String getRequestedSessionId() {
		
		return null;
	}

	public String getServletPath() {
		
		return servlet_path;
	}

	public HttpSession getSession() {
		
		return session;
	}

	public HttpSession getSession(boolean arg0) {
		if(arg0){
			if( session == null ){
				session = new MockSession();
			}else{
				session.is_new=false;
			}
		}
		
		return session;
	}

	public Principal getUserPrincipal() {
		
		return principal;
	}

	public boolean isRequestedSessionIdFromCookie() {
		
		return false;
	}

	public boolean isRequestedSessionIdFromURL() {
		
		return false;
	}

	@Deprecated
	public boolean isRequestedSessionIdFromUrl() {
		
		return false;
	}

	public boolean isRequestedSessionIdValid() {
		
		return false;
	}

	public boolean isUserInRole(String arg0) {
		
		return roles.contains(arg0);
	}

	public Object getAttribute(String arg0) {
		
		return attr.get(arg0);
	}

	public Enumeration getAttributeNames() {
		
		return null;
	}

	public String getCharacterEncoding() {
		
		return null;
	}

	public int getContentLength() {
		
		return 0;
	}

	public String getContentType() {
		
		return content_type;
	}

	public ServletInputStream getInputStream() throws IOException {
		
		return null;
	}

	public String getLocalAddr() {
		
		return null;
	}

	public String getLocalName() {
		
		return "localhost";
	}

	public int getLocalPort() {
		
		return 0;
	}

	public Locale getLocale() {
		
		return null;
	}

	public Enumeration getLocales() {
		
		return null;
	}

	public String getParameter(String arg0) {
		Object values = params.get(arg0);
		if( values == null ){
			return null;
		}
		if( values instanceof String){
			return (String) values;
		}
		Vector<String> v = (Vector<String>) values;
		return v.get(0);
	}

	public Map getParameterMap() {
		return params;
	}

	public Enumeration getParameterNames() {
		
		return params.keys();
	}

	public String[] getParameterValues(String arg0) {
		Object o = params.get(arg0);
		if( o == null ){
			return new String[0];
		}
		if( o instanceof String){
			return new String[] { (String) o};
		}
		Vector<String> v = (Vector<String>)o;
		return v.toArray(new String[0]);
	}

	public String getProtocol() {
		
		return null;
	}

	public BufferedReader getReader() throws IOException {
		
		return null;
	}


	@Deprecated
	public String getRealPath(String arg0) {
		
		return null;
	}

	public String getRemoteAddr() {
		
		return null;
	}

	public String getRemoteHost() {
		
		return "localhost";
	}

	public int getRemotePort() {
		
		return 0;
	}

	public RequestDispatcher getRequestDispatcher(String arg0) {
		
		return new MockRequestDispatcher(arg0);
	}

	public String getScheme() {
		
		return null;
	}

	public String getServerName() {
		
		return null;
	}

	public int getServerPort() {
		
		return 0;
	}

	public boolean isSecure() {
		
		return false;
	}

	public void removeAttribute(String arg0) {
		attr.remove(arg0);

	}

	public void setAttribute(String arg0, Object arg1) {
		attr.put(arg0,arg1);

	}

	public void setCharacterEncoding(String arg0)
			throws UnsupportedEncodingException {
		

	}
	public void addParameter(String key, String value){
		Object prev = params.get(key);
		if( prev == null){
			params.put(key, value);
			return;
		}else if( prev instanceof String){
			Vector<String> v = new Vector<String>();
			v.add((String)prev);
			v.add(value);
			params.put(key, v);
		}else{
			Vector<String> v = (Vector<String>)prev;
			v.add(value);
		}
	}
	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getAsyncContext()
	 */
	
	public AsyncContext getAsyncContext() {
		// TODO Auto-generated method stub
		return null;
	}
	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getDispatcherType()
	 */
	
	public DispatcherType getDispatcherType() {
		// TODO Auto-generated method stub
		return null;
	}
	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getServletContext()
	 */
	
	public ServletContext getServletContext() {
		// TODO Auto-generated method stub
		return null;
	}
	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#isAsyncStarted()
	 */
	
	public boolean isAsyncStarted() {
		// TODO Auto-generated method stub
		return false;
	}
	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#isAsyncSupported()
	 */
	
	public boolean isAsyncSupported() {
		// TODO Auto-generated method stub
		return false;
	}
	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#startAsync()
	 */
	
	public AsyncContext startAsync() {
		// TODO Auto-generated method stub
		return null;
	}
	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#startAsync(javax.servlet.ServletRequest, javax.servlet.ServletResponse)
	 */
	
	public AsyncContext startAsync(ServletRequest arg0, ServletResponse arg1) {
		// TODO Auto-generated method stub
		return null;
	}
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#authenticate(javax.servlet.http.HttpServletResponse)
	 */
	
	public boolean authenticate(HttpServletResponse arg0) throws IOException,
			ServletException {
		// TODO Auto-generated method stub
		return false;
	}
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#getPart(java.lang.String)
	 */
	
	public Part getPart(String arg0) throws IOException, IllegalStateException,
			ServletException {
		return parts.get(arg0);
	}
	
	public void addPart(Part p){
		content_type= "multipart/";
		parts.put(p.getName(), p);
	}
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#getParts()
	 */
	
	public Collection<Part> getParts() throws IOException,
			IllegalStateException, ServletException {
		return parts.values();
	}
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#login(java.lang.String, java.lang.String)
	 */
	
	public void login(String arg0, String arg1) throws ServletException {
		// TODO Auto-generated method stub
		
	}
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#logout()
	 */
	
	public void logout() throws ServletException {
		// TODO Auto-generated method stub
		
	}
	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getContentLengthLong()
	 */
	
	public long getContentLengthLong() {
		// TODO Auto-generated method stub
		return 0;
	}
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#changeSessionId()
	 */
	
	public String changeSessionId() {
		// TODO Auto-generated method stub
		return null;
	}
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#upgrade(java.lang.Class)
	 */
	
	public <T extends HttpUpgradeHandler> T upgrade(Class<T> arg0) throws IOException, ServletException {
		return null;
	}
}