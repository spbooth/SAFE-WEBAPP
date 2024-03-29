//| Copyright - The University of Edinburgh 2017                            |
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
package uk.ac.ed.epcc.webapp.servlet;

import uk.ac.ed.epcc.webapp.session.RandomService;

/** A not random at all version of {@link RandomService} for unit tests
 * @author spb
 *
 */
public class MockRandomService extends RandomService {

	
	private int i=0;
	
	private int pos=0;
	private byte byte_data[]=null;
	/**
	 * 
	 */
	public MockRandomService() {
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.session.RandomService#randomInt(int)
	 */
	@Override
	public int randomInt(int total_chars) {
		return (i++) % total_chars;
	}
	@Override
	public byte[] randomBytes(int length) {
		byte[] result = new byte[length];
		for(int i=0 ; i<length ; i++) {
			if( byte_data != null && pos < byte_data.length) {
				result[i] = byte_data[pos++];
			}else {
				result[i]=(byte) (i%255);
			}
		}
		return result;
		
	}
	
	public void setByteData(byte values[]) {
		pos=0;
		byte_data=values;
	}
}
