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

import uk.ac.ed.epcc.webapp.forms.inputs.CalendarFieldPeriodInput;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.time.CalendarFieldSplitPeriod;



public class CalendarFieldPeriodInputTest extends MultiInputTestBase<CalendarFieldSplitPeriod,Input,CalendarFieldPeriodInput> {

	@Override
	@SuppressWarnings("deprecation")
	public Set<CalendarFieldSplitPeriod> getGoodData() throws Exception {
		HashSet<CalendarFieldSplitPeriod> result = new HashSet<>();
		
		Calendar c = Calendar.getInstance();
		c.setTime(new Date(105,11,12));
		result.add(new CalendarFieldSplitPeriod(c,Calendar.DAY_OF_MONTH,1,4));
		
		return result;
	}

	@Override
	public Set<CalendarFieldSplitPeriod> getBadData() throws Exception {
		HashSet<CalendarFieldSplitPeriod> result = new HashSet<>();
		
		return result;
	}

	@Override
	public CalendarFieldPeriodInput getInput() throws Exception {
		
		return CalendarFieldPeriodInput.getInstance(ctx);
	}


	
	
	
}