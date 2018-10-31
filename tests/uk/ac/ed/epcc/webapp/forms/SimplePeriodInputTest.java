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

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


import uk.ac.ed.epcc.webapp.forms.inputs.SimplePeriodInput;
import uk.ac.ed.epcc.webapp.forms.inputs.TimeStampMultiInput;
import uk.ac.ed.epcc.webapp.time.CalendarFieldSplitPeriod;
import uk.ac.ed.epcc.webapp.time.Period;
import uk.ac.ed.epcc.webapp.time.RegularSplitPeriod;



public class SimplePeriodInputTest extends MultiInputTestBase<Period,TimeStampMultiInput,SimplePeriodInput> {

	@Override
	@SuppressWarnings("deprecation")
	public Set<Period> getGoodData() throws Exception {
		HashSet<Period> result = new HashSet<>();
		result.add(new Period(new Date(105,11,12),new Date(105,11,15)));
		result.add(new RegularSplitPeriod(new Date(105,11,12),new Date(105,11,15),4));
		Calendar c = Calendar.getInstance();
		c.setTime(new Date(105,11,12));
		result.add(new CalendarFieldSplitPeriod(c,Calendar.DAY_OF_YEAR,1,4));
		return result;
	}

	@Override
	public Set<Period> getBadData() throws Exception {
		HashSet<Period> result = new HashSet<>();
		
		return result;
	}

	@Override
	public SimplePeriodInput getInput() throws Exception {
		return new SimplePeriodInput();
	}
	
	
	
	
}