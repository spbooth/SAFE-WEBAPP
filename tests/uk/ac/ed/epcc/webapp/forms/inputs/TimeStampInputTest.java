/*******************************************************************************
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms.inputs;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.forms.ParseAbstractInputTestCase;
import uk.ac.ed.epcc.webapp.forms.TestParseDataProvider;
import uk.ac.ed.epcc.webapp.forms.inputs.TimeStampInput;
public class TimeStampInputTest extends ParseAbstractInputTestCase<Date,TimeStampInput>  implements TestParseDataProvider<Date,TimeStampInput>{

	@Test
	public void dummy(){
		
	}
	public TimeStampInput getInput() {
	
		return  new TimeStampInput(1000L);
	}

	
	public Set<Date> getBadData() {
		return new HashSet<Date>();
	}

	
	public Set<Date> getGoodData() {
		HashSet<Date> good = new HashSet<Date>();
		Date r = new Date();
		r.setTime((r.getTime()/1000)*1000);
		good.add(r);
		return good;
	}

	

	public Set<String> getBadParseData() {
		Set<String> res = new HashSet<String>();
		res.add("12-12-2008 08:00:00");
		res.add("boris the spider");
		return res;
	}


	public Set<String> getGoodParseData() {
		Set<String> res = new HashSet<String>();
		res.add("2006-12-12 08:00:00");
		res.add("2006-12-12 08:00");
		res.add("2006-12-12 08");
		res.add("2006-12-12");
		return res;
	}
}
