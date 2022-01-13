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

import java.io.IOException;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;

/**
 * @author Stephen Booth
 *
 */
public class MockInputStream extends ServletInputStream {

	/**
	 * @param data
	 */
	public MockInputStream(byte[] data) {
		super();
		this.data = data;
	}

	private final byte data[];
	int pos=0;
	/* (non-Javadoc)
	 * @see javax.servlet.ServletInputStream#isFinished()
	 */
	@Override
	public boolean isFinished() {
		return pos < data.length;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletInputStream#isReady()
	 */
	@Override
	public boolean isReady() {
		return true;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletInputStream#setReadListener(javax.servlet.ReadListener)
	 */
	@Override
	public void setReadListener(ReadListener readListener) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#read()
	 */
	@Override
	public int read() throws IOException {
		if( pos < data.length) {
			return data[pos++];
		}
		return -1;
	}

}
