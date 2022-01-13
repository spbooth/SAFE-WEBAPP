//| Copyright - The University of Edinburgh 2012                            |
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

import java.util.HashSet;
import java.util.Set;

import uk.ac.ed.epcc.webapp.forms.MultiInputTestBase;
import uk.ac.ed.epcc.webapp.forms.ParseMapInputInterfaceTest;
import uk.ac.ed.epcc.webapp.forms.ParseMapInputInterfaceTestImpl;


/**
 * @author spb
 *
 */

public class AlternateInputTestCase extends MultiInputTestBase<Integer,Input<Integer>,AlternateInput<Integer>> implements
ParseMapInputInterfaceTest<Integer, AlternateInput<Integer>, AlternateInputTestCase>

{

	
	public ParseMapInputInterfaceTest<Integer, AlternateInput<Integer>, AlternateInputTestCase> parse_map_test = new ParseMapInputInterfaceTestImpl<>(this);
	
		/**
	 * 
	 */
	public AlternateInputTestCase() {
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.TestDataProvider#getGoodData()
	 */
	@Override
	public Set<Integer> getGoodData() throws Exception {
		HashSet<Integer> result = new HashSet<>();
		for(int i = 1; i< 10; i++){
			result.add(i);
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.TestDataProvider#getBadData()
	 */
	@Override
	public Set<Integer> getBadData() throws Exception {
		HashSet<Integer> result = new HashSet<>();
		result.add(-1);
		return result;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.TestDataProvider#getInput()
	 */
	@Override
	public AlternateInput<Integer> getInput() throws Exception {
		AlternateInput<Integer> result = new AlternateInput<>();
		
		
		result.addInput("PullDown", new IntegerSetInput(new int[]{3,5,7}));
		IntegerInput i = new IntegerInput();
		i.setMin(0);
		result.addInput("Free", i);
		return result;
	}

	

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.ParseMapInputInterfaceTest#testGoodDataParses()
	 */
	@Override
	public void testGoodDataParses() throws Exception {
		parse_map_test.testGoodDataParses();
		
	}



}