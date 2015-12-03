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
package uk.ac.ed.epcc.webapp.forms.inputs;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;





import uk.ac.ed.epcc.webapp.forms.inputs.PercentInput;

public class PercentInputTestCase extends NumberInputTestCase<Double, PercentInput> {

	@Override
	protected String getExpectedType() {
		return null;
	}

	public Set<String> getGoodParseData() {
		Set<String> good=new HashSet<String>();
		good.add("0%");
		good.add("100%");
		good.add("55%");
		return good;
	}

	public Set<String> getBadParseData() {
		Set<String> bad = new HashSet<String>();
		bad.add("boris");
		bad.add("110");
		bad.add("-7");
		bad.add("110%");
		bad.add("-7%");
		return bad;
	}

	public Set<Double> getGoodData() throws Exception {
		Set<Double> good = new HashSet<Double>();
		good.add(0.0);
		good.add(1.0);
		good.add(0.75);
		return good;
	}

	public Set<Double> getBadData() throws Exception {
		Set<Double> bad = new HashSet<Double>();
		bad.add(-0.3);
		bad.add(7.0);
		return bad;
	}

	public PercentInput getInput() throws Exception {
		
		return new PercentInput();
	}
	
	@org.junit.Test
	public void testRange() throws Exception{
		PercentInput input = getInput();
		assertEquals("0",input.formatRange(input.getMin()));
		assertEquals("100",input.formatRange(input.getMax()));
		assertEquals("1",input.formatRange(input.getStep()));
	}

}