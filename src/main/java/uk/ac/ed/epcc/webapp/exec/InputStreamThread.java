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
package uk.ac.ed.epcc.webapp.exec;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/** Thread to consume an {@link InputStream}.
 * @author spb
 *
 */

public class InputStreamThread extends Thread {
	

	/**
	 * @param stream
	 */
	public InputStreamThread(InputStream stream) {
		super();
		this.stream = stream;
		result = new ByteArrayOutputStream();
	}

	private final InputStream stream;
	private final ByteArrayOutputStream result;

	@Override
	public void run() {
		byte[] buffer = new byte[512];
		int bytes_read = 0;
		try {
			while( (bytes_read = stream.read(buffer, 0, 512)) != -1){
				result.write(buffer, 0, bytes_read);
			}
		} catch (IOException e) {
			return;
			
		}
		
		
	}
	
	public byte[] getBytes(){
		return result.toByteArray();
	}
	
	public String getString(){
		return result.toString();
	}
}