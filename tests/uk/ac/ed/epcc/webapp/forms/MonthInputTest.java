/*******************************************************************************
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;

import uk.ac.ed.epcc.webapp.forms.inputs.MonthInput;


public class MonthInputTest<I extends MonthInput> extends ParseAbstractInputTestCase<Date, I>  {
	@Test
	public void dummy(){
		
	}
	public Set<String> getBadParseData() {
		HashSet<String> set=new HashSet<String>();
		set.add("womble");
		set.add("01-01-2008");
		return set;
	}

	public Set<String> getGoodParseData() {
		HashSet<String> set=new HashSet<String>();
		set.add("12-1965");
		set.add("09-2008");
		return set;
	}

	public Set<Date> getBadData() throws Exception {
		HashSet<Date> set = new HashSet<Date>();
		return set;
	}

	@SuppressWarnings("deprecation")
	public Set<Date> getGoodData() throws Exception {
		HashSet<Date> set = new HashSet<Date>();
		set.add(new Date(2008,10,01));
		return set;
	}

	@SuppressWarnings("unchecked")
	public I getInput() {
		return (I) new MonthInput();
	}
   

}
