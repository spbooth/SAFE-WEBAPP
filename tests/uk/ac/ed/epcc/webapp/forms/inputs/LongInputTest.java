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

import uk.ac.ed.epcc.webapp.forms.inputs.LongInput;
public class LongInputTest extends NumberInputTestCase<Long,LongInput> {
	@Test
	public void dummy(){
		
	}
	public Set<Long> getBadData() {
		Set<Long> bad = new HashSet<Long>();
		bad.add(-1L);
		bad.add(2L);
		bad.add(500L);
		return bad;
	}

	public Set<Long> getGoodData() {
		Set<Long> good = new HashSet<Long>();
		good.add(12L);
		good.add(14L);
		return good;
	}

	public LongInput getInput() {
		LongInput i = new LongInput();
		i.setMin(10L);
		i.setMax(100L);
		return i;
	}
	
	public Set<String> getBadParseData() {
        HashSet<String> res = new HashSet<String>();
		res.add("fred");
		res.add("14.5.6");
		res.add("82wombat");
		res.add("2");
		return res;

	}


	public Set<String> getGoodParseData() {
		HashSet<String> res = new HashSet<String>();
		res.add("12");
		res.add("15");
		res.add("70");
		return res;
	}
	
	

}