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

import uk.ac.ed.epcc.webapp.servlet.WebappServlet;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;

public class MockRequest implements HttpServletRequest {
    public Hashtable<String,Object> params=new Hashtable<>();
    public HashMap<String,Object> attr = new HashMap<>();
    public HashMap<String,String> header = new HashMap<>();
    public Set<String> roles = new HashSet<>();
    public Map<String,Part> parts = new HashMap<>();
    public String context_path;
    public String servlet_path;
    public String path_info="";
    public String method="POST";
    public MockSession session;
    public String remote_user=null;
    public String content_type="application/x-www-form-urlencoded";
    public Principal principal=null;
    public MockInputStream stream=null;
    public MockRequest(String path){
    	context_path=path;
    }
	@Override
	public String getAuthType() {
		
		return null;
	}

	@Override
	public String getContextPath() {
		return context_path;
	}
	public void setContextPath(String path){
		context_path=path;
	}

	public Cookie cookies[] = null;
	@Override
	public Cookie[] getCookies() {
		
		return cookies;
	}

	@Override
	public long getDateHeader(String arg0) {
		
		return 0;
	}

	@Override
	public String getHeader(String arg0) {
		
		return header.get(arg0);
	}

	@Override
	public Enumeration getHeaderNames() {
		
		return Collections.enumeration(header.keySet());
	}

	@Override
	public Enumeration getHeaders(String arg0) {
		
		return null;
	}

	@Override
	public int getIntHeader(String arg0) {
		
		return 0;
	}

	@Override
	public String getMethod() {
		
		return method;
	}

	@Override
	public String getPathInfo() {
		
		return path_info;
	}

	@Override
	public String getPathTranslated() {
		
		return null;
	}

	@Override
	public String getQueryString() {
		if( method.equalsIgnoreCase("GET")) {
			StringBuilder sb = new StringBuilder();
			for(Map.Entry<String,Object> e : params.entrySet()) {
				if(sb.length()>0) {
					sb.append("&");
				}
				sb.append(e.getKey());
				sb.append("=");
				sb.append(WebappServlet.encodeCGI(e.getValue().toString()));
			}
			return sb.toString();
		}
		return null;
	}

	@Override
	public String getRemoteUser() {
		
		return remote_user;
	}

	@Override
	public String getRequestURI() {
		// pathinfo is supposed to start with a /
		String pathInfo = getPathInfo();
		if( ! pathInfo.startsWith("/")) {
			pathInfo="/"+pathInfo;
		}
		return getServletPath()+pathInfo;
	}

	@Override
	public StringBuffer getRequestURL() {
		
		return null;
	}

	@Override
	public String getRequestedSessionId() {
		
		return null;
	}

	@Override
	public String getServletPath() {
		
		return servlet_path;
	}

	@Override
	public HttpSession getSession() {
		
		return getSession(true);
	}

	@Override
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

	@Override
	public Principal getUserPrincipal() {
		
		return principal;
	}

	@Override
	public boolean isRequestedSessionIdFromCookie() {
		
		return false;
	}

	@Override
	public boolean isRequestedSessionIdFromURL() {
		
		return false;
	}

	@Override
	@Deprecated
	public boolean isRequestedSessionIdFromUrl() {
		
		return false;
	}

	@Override
	public boolean isRequestedSessionIdValid() {
		
		return false;
	}

	@Override
	public boolean isUserInRole(String arg0) {
		
		return roles.contains(arg0);
	}

	@Override
	public Object getAttribute(String arg0) {
		
		return attr.get(arg0);
	}

	private static class EnumerationWrapper<E> implements Enumeration<E>{
		/**
		 * @param it
		 */
		public EnumerationWrapper(Iterator<E> it) {
			super();
			this.it = it;
		}

		private final Iterator<E> it;

		/* (non-Javadoc)
		 * @see java.util.Enumeration#hasMoreElements()
		 */
		@Override
		public boolean hasMoreElements() {
			return it.hasNext();
		}

		/* (non-Javadoc)
		 * @see java.util.Enumeration#nextElement()
		 */
		@Override
		public E nextElement() {
			return it.next();
		}
	}
	@Override
	public Enumeration getAttributeNames() {
		
		return new EnumerationWrapper<String>(attr.keySet().iterator());
	}

	@Override
	public String getCharacterEncoding() {
		
		return null;
	}

	@Override
	public int getContentLength() {
		
		return 0;
	}

	@Override
	public String getContentType() {
		
		return content_type;
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		
		return stream;
	}

	@Override
	public String getLocalAddr() {
		
		return null;
	}

	@Override
	public String getLocalName() {
		
		return "localhost";
	}

	@Override
	public int getLocalPort() {
		
		return 443;
	}

	@Override
	public Locale getLocale() {
		
		return null;
	}

	@Override
	public Enumeration getLocales() {
		
		return null;
	}

	@Override
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

	@Override
	public Map getParameterMap() {
		Map<String,String[]> result = new LinkedHashMap<String, String[]>();
		for(Map.Entry e : params.entrySet()) {
			Object o = e.getValue();
			if( o instanceof String[]) {
				result.put((String)e.getKey(),(String[]) o);
			}else if ( o instanceof String) {
				result.put((String) e.getKey(), new String[] { (String) e.getValue() });
			}
		}
		return Collections.unmodifiableMap(result);
	}

	@Override
	public Enumeration getParameterNames() {
		
		return params.keys();
	}

	@Override
	public String[] getParameterValues(String arg0) {
		Object o = params.get(arg0);
		if( o == null ){
			return new String[0];
		}
		if( o instanceof String){
			return new String[] { (String) o};
		}
		if( o instanceof Vector) {
			Vector<String> v = (Vector<String>)o;
			return v.toArray(new String[0]);
		}
		return new String[0];
	}

	@Override
	public String getProtocol() {
		
		return "https";
	}

	@Override
	public BufferedReader getReader() throws IOException {
		
		return null;
	}


	@Override
	@Deprecated
	public String getRealPath(String arg0) {
		
		return null;
	}

	@Override
	public String getRemoteAddr() {
		
		return null;
	}

	@Override
	public String getRemoteHost() {
		
		return "localhost";
	}

	@Override
	public int getRemotePort() {
		
		return 0;
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String arg0) {
		
		return new MockRequestDispatcher(arg0);
	}

	@Override
	public String getScheme() {
		
		return null;
	}

	@Override
	public String getServerName() {
		
		return null;
	}

	@Override
	public int getServerPort() {
		
		return 0;
	}

	@Override
	public boolean isSecure() {
		
		return true;
	}

	@Override
	public void removeAttribute(String arg0) {
		attr.remove(arg0);

	}

	@Override
	public void setAttribute(String arg0, Object arg1) {
		attr.put(arg0,arg1);

	}

	@Override
	public void setCharacterEncoding(String arg0)
			throws UnsupportedEncodingException {
		

	}
	public void addParameter(String key, String value){
		Object prev = params.get(key);
		if( prev == null){
			params.put(key, value);
			return;
		}else if( prev instanceof String){
			Vector<String> v = new Vector<>();
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
	
	@Override
	public AsyncContext getAsyncContext() {
		// TODO Auto-generated method stub
		return null;
	}
	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getDispatcherType()
	 */
	
	@Override
	public DispatcherType getDispatcherType() {
		// TODO Auto-generated method stub
		return null;
	}
	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getServletContext()
	 */
	
	@Override
	public ServletContext getServletContext() {
		// TODO Auto-generated method stub
		return null;
	}
	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#isAsyncStarted()
	 */
	
	@Override
	public boolean isAsyncStarted() {
		// TODO Auto-generated method stub
		return false;
	}
	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#isAsyncSupported()
	 */
	
	@Override
	public boolean isAsyncSupported() {
		// TODO Auto-generated method stub
		return false;
	}
	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#startAsync()
	 */
	
	@Override
	public AsyncContext startAsync() {
		// TODO Auto-generated method stub
		return null;
	}
	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#startAsync(javax.servlet.ServletRequest, javax.servlet.ServletResponse)
	 */
	
	@Override
	public AsyncContext startAsync(ServletRequest arg0, ServletResponse arg1) {
		// TODO Auto-generated method stub
		return null;
	}
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#authenticate(javax.servlet.http.HttpServletResponse)
	 */
	
	@Override
	public boolean authenticate(HttpServletResponse arg0) throws IOException,
			ServletException {
		// TODO Auto-generated method stub
		return false;
	}
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#getPart(java.lang.String)
	 */
	
	@Override
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
	
	@Override
	public Collection<Part> getParts() throws IOException,
			IllegalStateException, ServletException {
		return parts.values();
	}
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#login(java.lang.String, java.lang.String)
	 */
	
	@Override
	public void login(String arg0, String arg1) throws ServletException {
		// TODO Auto-generated method stub
		
	}
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#logout()
	 */
	
	@Override
	public void logout() throws ServletException {
		// TODO Auto-generated method stub
		
	}
	/* (non-Javadoc)
	 * @see javax.servlet.ServletRequest#getContentLengthLong()
	 */
	
	@Override
	public long getContentLengthLong() {
		// TODO Auto-generated method stub
		return 0;
	}
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#changeSessionId()
	 */
	
	@Override
	public String changeSessionId() {
		// TODO Auto-generated method stub
		return null;
	}
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletRequest#upgrade(java.lang.Class)
	 */
	
	@Override
	public <T extends HttpUpgradeHandler> T upgrade(Class<T> arg0) throws IOException, ServletException {
		return null;
	}
}