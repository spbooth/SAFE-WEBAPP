//| Copyright - The University of Edinburgh 2019                            |
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
package uk.ac.ed.epcc.webapp;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.util.EnumSet;

import org.junit.Test;

/**
 * @author Stephen Booth
 *
 */
public class UnitFormatTest {

	/**
	 * 
	 */
	public UnitFormatTest() {
		// TODO Auto-generated constructor stub
	}
	
	@Test
	public void testParse() throws ParseException {
		
		UnitFormat format = new UnitFormat();
		
		for(int i=0; i<100;i++) {
			for(Units u : EnumSet.allOf(Units.class)) {
				long expected = i * u.bytes;
				String input = Integer.toString(i)+u.toString();
				System.out.println(input);
				Number num = format.parse(input);
				assertEquals(input, expected, num.longValue());
				String formatted = format.format(num);
				System.out.println(formatted);
				assertEquals(formatted ,   expected, format.parse(formatted).longValue());
				
				
			}
			
		}
		
	}

}
