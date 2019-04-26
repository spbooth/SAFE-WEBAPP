//| Copyright - The University of Edinburgh 2018                            |
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
package uk.ac.ed.epcc.webapp.forms.inputs;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.forms.AbstractInputTestCase;

/**
 * @author Stephen Booth
 *
 */
public class PatternInputTest extends AbstractInputTestCase<String,PatternTextInput> {

	/**
	 * 
	 */
	private static final String PATTERN = "AB{0,3}C";

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.TestDataProvider#getGoodData()
	 */
	@Override
	public Set<String> getGoodData() throws Exception {
		Set<String> res = new HashSet<>();
		res.add("AC");
		res.add("ABC");
		res.add("ABBC");
		res.add("ABBBC");
		return res;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.TestDataProvider#getBadData()
	 */
	@Override
	public Set<String> getBadData() throws Exception {
		Set<String> res = new HashSet<>();
		res.add("boris");
		res.add("http www.example.com fred");
		res.add("ACD");
		res.add("ABBBBC");
		return res;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.TestDataProvider#getInput()
	 */
	@Override
	public PatternTextInput getInput() throws Exception {
		return new PatternTextInput(PATTERN);
	}
	
	@Test
	public void testPattern() throws Exception {
		assertEquals(PATTERN,getInput().getPattern());
	}

	@Test
	public void testTag() throws Exception {
		PatternTextInput input = getInput();
		assertEquals("Regexp: "+PATTERN,input.getTag());
		input.setTag("emu");
		assertEquals("emu", input.getTag());
	}
	
}
