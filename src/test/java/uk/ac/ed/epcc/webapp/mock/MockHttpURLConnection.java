//| Copyright - The University of Edinburgh 2020                            |
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
package uk.ac.ed.epcc.webapp.mock;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Stephen Booth
 *
 */
public class MockHttpURLConnection extends HttpURLConnection {

	

	@Override
	public String getHeaderField(String name) {
		return headers.get(name);
	}

	
    String status;
	LinkedHashMap<String,String> headers=new LinkedHashMap<String, String>(); 
	
	public byte[] response;
	@Override
	public InputStream getInputStream() throws IOException {
		if( in != null) {
			return in;
		}
		return super.getInputStream();
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		if( out != null) {
			return out;
		}
		return super.getOutputStream();
	}

	/**
	 * @param u
	 */
	protected MockHttpURLConnection(URL u) {
		super(u);
	}

	ByteArrayOutputStream out=null;
	ByteArrayInputStream in = null;
	/* (non-Javadoc)
	 * @see java.net.HttpURLConnection#disconnect()
	 */
	@Override
	public void disconnect() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.net.HttpURLConnection#usingProxy()
	 */
	@Override
	public boolean usingProxy() {
		return false;
	}

	/* (non-Javadoc)
	 * @see java.net.URLConnection#connect()
	 */
	@Override
	public void connect() throws IOException {
		if( response != null) {
			in = new ByteArrayInputStream(response);
		}
		if( getDoOutput()) {
			out = new ByteArrayOutputStream();
		}
	}

	
	
	public String getOutput() {
		if( out == null) {
			return "";
		}
		return out.toString();
	}
	
	public void setReponse(byte[] response) {
		this.response = response;
	}
	public void setStatus(String status) {
		this.status=status;
	}
	public void addResponseHeader(String fiels,String value  ) {
		headers.put(fiels,value);
	}
	public void addStatusLine(String line) {
		status=line;
	}

	@Override
	public String getHeaderFieldKey(int n) {
		if( n == 0 ) {
			return null;
		}
		int i=1;
		for(String s : headers.keySet()) {
			if( i == n) {
				return s;
			}
			i++;
		}
		
		return null;
	}

	@Override
	public String getHeaderField(int n) {
		if( n == 0) {
			return status;
		}
		int i=1;
		for(String s : headers.values()) {
			if( i == n) {
				return s;
			}
			i++;
		}
		
		return null;
	}
}
