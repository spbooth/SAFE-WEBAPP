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

import uk.ac.ed.epcc.webapp.forms.inputs.RealInput;

public class RealInputTest extends NumberInputTestCase<Float,RealInput> {
	@Test
	public void dummy(){
		
	}
	public Set<Float> getGoodData() {
		HashSet<Float> good = new HashSet<Float>();
		good.add(12.0f);
		good.add(14.0f);
		return good;
	}


	public RealInput getInput() {
		RealInput realInput = new RealInput();
		realInput.setMin(0.0f);
		return realInput;
	}


	public Set<Float> getBadData() {
		return new HashSet<Float>();
	}
	


	public Set<String> getBadParseData() {
        HashSet<String> res = new HashSet<String>();
		res.add("fred");
		res.add("14.5.6");
		res.add("82wombat");
		res.add("-1.0");
		return res;

	}


	public Set<String> getGoodParseData() {
		HashSet<String> res = new HashSet<String>();
		res.add("12.8");
		res.add("0.0");
		res.add("0");
		return res;
	}
}