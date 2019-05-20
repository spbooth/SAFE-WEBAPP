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

import java.util.HashSet;
import java.util.Set;

import uk.ac.ed.epcc.webapp.forms.ParseAbstractInputTestCase;
import uk.ac.ed.epcc.webapp.forms.inputs.ElapsedSecondInput;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;

public class ElapsedSecondTestInput extends ParseAbstractInputTestCase<Number,ElapsedSecondInput> {

	@Override
	public Set<String> getGoodParseData() {
		HashSet<String> res = new HashSet<>();
		res.add("5");
		res.add("5:06");
		res.add("6:00:00");
		return res;
	}

	@Override
	public Set<String> getBadParseData() {
		HashSet<String> res = new HashSet<>();
		res.add("-17");
		res.add("10:00:00:00");
		res.add("wombat");
		return res;
	}

	@Override
	public Set<Number> getGoodData() throws Exception {
		HashSet<Number> res = new HashSet<>();
		res.add(5L);
		res.add(60L);
		res.add(8000L);
		return res;
	}

	@Override
	public Set<Number> getBadData() throws Exception {
		HashSet<Number> res = new HashSet<>();
		res.add(-3);
		return res;
	}

	@Override
	public ElapsedSecondInput getInput() throws Exception {
		return new ElapsedSecondInput();
	}

	

}