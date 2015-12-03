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
	public Set<Integer> getBadData() {
		Set<Integer> bad = new HashSet<Integer>();
		bad.add(-1);
		return bad;
	}

	public Set<Integer> getGoodData() {
		Set<Integer> good = new HashSet<Integer>();
		good.add(12);
		good.add(14);
		return good;
	}

	public IntegerInput getInput() {
		IntegerInput i = new IntegerInput();
		i.setMin(0);
		return i;
	}
	
	public Set<String> getBadParseData() {
        HashSet<String> res = new HashSet<String>();
		res.add("fred");
		res.add("14.5.6");
		res.add("82wombat");
		return res;

	}


	public Set<String> getGoodParseData() {
		HashSet<String> res = new HashSet<String>();
		res.add("12");
		res.add("1");
		res.add("0");
		return res;
	}
	
	

}