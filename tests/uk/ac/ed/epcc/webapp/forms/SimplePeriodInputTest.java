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

	@SuppressWarnings("deprecation")
	public Set<Period> getGoodData() throws Exception {
		HashSet<Period> result = new HashSet<Period>();
		result.add(new Period(new Date(105,11,12),new Date(105,11,15)));
		result.add(new RegularSplitPeriod(new Date(105,11,12),new Date(105,11,15),4));
		Calendar c = Calendar.getInstance();
		c.setTime(new Date(105,11,12));
		result.add(new CalendarFieldSplitPeriod(c,Calendar.DAY_OF_YEAR,1,4));
		return result;
	}

	public Set<Period> getBadData() throws Exception {
		HashSet<Period> result = new HashSet<Period>();
		
		return result;
	}

	public SimplePeriodInput getInput() throws Exception {
		return new SimplePeriodInput();
	}
	
	
	
	
}
