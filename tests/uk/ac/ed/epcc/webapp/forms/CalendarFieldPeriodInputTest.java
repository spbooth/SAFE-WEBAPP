package uk.ac.ed.epcc.webapp.forms;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import uk.ac.ed.epcc.webapp.forms.inputs.CalendarFieldPeriodInput;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.time.CalendarFieldSplitPeriod;



public class CalendarFieldPeriodInputTest extends MultiInputTestBase<CalendarFieldSplitPeriod,Input,CalendarFieldPeriodInput> {

	@SuppressWarnings("deprecation")
	public Set<CalendarFieldSplitPeriod> getGoodData() throws Exception {
		HashSet<CalendarFieldSplitPeriod> result = new HashSet<CalendarFieldSplitPeriod>();
		
		Calendar c = Calendar.getInstance();
		c.setTime(new Date(105,11,12));
		result.add(new CalendarFieldSplitPeriod(c,Calendar.DAY_OF_MONTH,1,4));
		
		return result;
	}

	public Set<CalendarFieldSplitPeriod> getBadData() throws Exception {
		HashSet<CalendarFieldSplitPeriod> result = new HashSet<CalendarFieldSplitPeriod>();
		
		return result;
	}

	public CalendarFieldPeriodInput getInput() throws Exception {
		
		return new CalendarFieldPeriodInput();
	}


	
	
	
}
