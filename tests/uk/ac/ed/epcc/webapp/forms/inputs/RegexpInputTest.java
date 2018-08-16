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
public class RegexpInputTest extends AbstractInputTestCase<String,RegexpInput> {

	

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.TestDataProvider#getGoodData()
	 */
	@Override
	public Set<String> getGoodData() throws Exception {
		Set<String> res = new HashSet<>();
		res.add("AC");
		res.add("A[BC]*");
		res.add("\\s*\\S+\\s*");
		return res;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.TestDataProvider#getBadData()
	 */
	@Override
	public Set<String> getBadData() throws Exception {
		Set<String> res = new HashSet<>();
		res.add("((((");
		res.add("[[");
		res.add("");
		return res;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.TestDataProvider#getInput()
	 */
	@Override
	public RegexpInput getInput() throws Exception {
		return new RegexpInput();
	}
	
	

	@Test
	public void testTag() throws Exception {
		RegexpInput input = getInput();
		assertEquals("(Regular expression)",input.getTag());
	}
	
}
