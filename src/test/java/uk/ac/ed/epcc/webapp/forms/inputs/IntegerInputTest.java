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
/*******************************************************************************
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.inputs;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.forms.inputs.IntegerInput;
public class IntegerInputTest extends NumberInputTestCase<Integer,IntegerInput> {
	@Test
	public void dummy(){
		
	}
	@Override
	public Set<Integer> getBadData() {
		Set<Integer> bad = new HashSet<>();
		bad.add(-1);
		return bad;
	}

	@Override
	public Set<Integer> getGoodData() {
		Set<Integer> good = new HashSet<>();
		good.add(12);
		good.add(14);
		return good;
	}

	@Override
	public IntegerInput getInput() {
		IntegerInput i = new IntegerInput();
		i.setMin(0);
		return i;
	}
	
	@Override
	public Set<String> getBadParseData() {
        HashSet<String> res = new HashSet<>();
		res.add("fred");
		res.add("14.5.6");
		res.add("82wombat");
		return res;

	}


	@Override
	public Set<String> getGoodParseData() {
		HashSet<String> res = new HashSet<>();
		res.add("12");
		res.add("1");
		res.add("0");
		return res;
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.BoundedInputDataProvider#getLowBound()
	 */
	@Override
	public Integer getLowBound() {
		return 10;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.BoundedInputDataProvider#getHighBound()
	 */
	@Override
	public Integer getHighBound() {
		return 20;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.BoundedInputDataProvider#getHighData()
	 */
	@Override
	public Set<Integer> getHighData() {
		Set<Integer> high = new HashSet<>();
		high.add(25);
		high.add(30);
		return high;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.inputs.BoundedInputDataProvider#getLowData()
	 */
	@Override
	public Set<Integer> getLowData() {
		Set<Integer> high = new HashSet<>();
		high.add(1);
		high.add(4);
		return high;
	}

}