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
package uk.ac.ed.epcc.webapp.forms;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.inputs.RegularPeriodInput;

import uk.ac.ed.epcc.webapp.time.RegularSplitPeriod;


public class RegularSplitPeriodInputTest extends MultiInputTestBase<RegularSplitPeriod,Input,RegularPeriodInput> {

	@SuppressWarnings("deprecation")
	public Set<RegularSplitPeriod> getGoodData() throws Exception {
		HashSet<RegularSplitPeriod> result = new HashSet<RegularSplitPeriod>();
		
		result.add(new RegularSplitPeriod(new Date(105,11,12),new Date(105,11,15),4));
		
		return result;
	}

	public Set<RegularSplitPeriod> getBadData() throws Exception {
		HashSet<RegularSplitPeriod> result = new HashSet<RegularSplitPeriod>();
		
		return result;
	}

	public RegularPeriodInput getInput() throws Exception {
		return new RegularPeriodInput();
	}
	
	
	
	
}