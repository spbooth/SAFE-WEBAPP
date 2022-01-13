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
package uk.ac.ed.epcc.webapp.mock;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;

/**
 * @author spb
 *
 */

public class MockOutputStream extends ServletOutputStream {
	

	ByteArrayOutputStream inner = new ByteArrayOutputStream();
	boolean is_closed=false;
	/**
	 * 
	 */
	public MockOutputStream() {
		
	}

	/* (non-Javadoc)
	 * @see java.io.OutputStream#write(int)
	 */
	@Override
	public void write(int b) throws IOException {
		if( is_closed) {
			throw new IOException("stream is closed");
		}
		inner.write(b);
	}

	public String toString(){
		return inner.toString();
	}

	public byte[] getData() {
		return inner.toByteArray();
	}
	/* (non-Javadoc)
	 * @see javax.servlet.ServletOutputStream#isReady()
	 */

	public boolean isReady() {
		return true;
	}
	@Override
	public void close() throws IOException {
		super.close();
		is_closed=true;
	}
	/* (non-Javadoc)
	 * @see javax.servlet.ServletOutputStream#setWriteListener(javax.servlet.WriteListener)
	 */
	
	public void setWriteListener(WriteListener arg0) {
		
	}
	
	public boolean isClosed() {
		return is_closed;
	}
}