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
