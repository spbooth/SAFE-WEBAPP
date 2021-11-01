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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class MockResponse implements HttpServletResponse {
	public String redirect;
	public String forward;

	public int error=HttpServletResponse.SC_OK;
	public String error_str=null;
	public boolean output_added=false;
	public MockOutputStream stream = new MockOutputStream();
	public String encoding="UTF-8";
	public String content_type="text/html";
	public Locale locale=Locale.getDefault();
	int content_length=0;
	Map<String,String> headers=new HashMap<>();
	public void addCookie(Cookie arg0) {
	

	}

	public void addDateHeader(String arg0, long arg1) {
		Date d = new Date(arg1);
		addHeader(arg0, d.toString());
	}

	public void addHeader(String arg0, String arg1) {
		headers.put(arg0, arg1);

	}

	public void addIntHeader(String arg0, int arg1) {
		addHeader(arg0, Integer.toString(arg1));

	}

	public boolean containsHeader(String arg0) {
		return headers.containsKey(arg0);
	}

	public String encodeRedirectURL(String arg0) {
		return arg0;
	}

	
	public String encodeRedirectUrl(String arg0) {
		return arg0;
	}

	public String encodeURL(String arg0) {
		return arg0;
	}

	
	public String encodeUrl(String arg0) {
		return arg0;
	}

	public void sendError(int arg0) throws IOException {
		sendError(arg0, "Unspewcified error");
	}

	public void sendError(int arg0, String arg1) throws IOException {
		if( output_added) {
			throw new IllegalStateException("Output already added");
		}
		this.error=arg0;
		this.error_str = arg1;

	}

	public void sendRedirect(String arg0) throws IOException {
		forward=null;
		redirect=arg0;
	}

	public void setDateHeader(String arg0, long arg1) {
		Date d = new Date(arg1);
		setHeader(arg0, d.toString());
	}

	public void setHeader(String arg0, String arg1) {
		headers.put(arg0, arg1);

	}

	public void setIntHeader(String arg0, int arg1) {
		headers.put(arg0, Integer.toString(arg1));

	}

	public void setStatus(int arg0) {
		error=arg0;
	}

	public void setStatus(int arg0, String arg1) {
		error=arg0;
		error_str=arg1;
	}

	public void flushBuffer() throws IOException {
		

	}

	public int getBufferSize() {
		
		return 0;
	}

	public String getCharacterEncoding() {
	
		return encoding;
	}

	public String getContentType() {
		
		return content_type;
	}

	public Locale getLocale() {
		
		return locale;
	}

	public ServletOutputStream getOutputStream() throws IOException {
		output_added=true;
		return stream;
	}

	PrintWriter writer = null;
	public PrintWriter getWriter() throws IOException {
		if( writer == null) {
			writer = new PrintWriter(getOutputStream());
		}
		return writer;
	}

	public boolean isCommitted() {
		
		return output_added;
	}

	public void reset() {
		

	}

	public void resetBuffer() {
		

	}

	public void setBufferSize(int arg0) {
		

	}

	public void setCharacterEncoding(String arg0) {
		encoding=arg0;
	}

	public void setContentLength(int arg0) {
		content_length=arg0;

	}

	public void setContentType(String arg0) {
		content_type=arg0;

	}

	public void setLocale(Locale arg0) {
		locale=arg0;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletResponse#getHeader(java.lang.String)
	 */
	
	public String getHeader(String arg0) {
		return headers.get(arg0);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletResponse#getHeaderNames()
	 */
	
	public Collection<String> getHeaderNames() {
		return headers.keySet();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletResponse#getHeaders(java.lang.String)
	 */
	
	public Collection<String> getHeaders(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletResponse#getStatus()
	 */
	
	public int getStatus() {
		return error;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletResponse#setContentLengthLong(long)
	 */
	
	public void setContentLengthLong(long arg0) {
		
		
	}

}