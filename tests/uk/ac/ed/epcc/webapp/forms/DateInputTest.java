/*******************************************************************************
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.forms.inputs.DateInput;

public class DateInputTest<I extends DateInput> extends ParseAbstractInputTestCase<Date, I>  {
	@Test
	public void dummy(){
		
	}
	public Set<String> getBadParseData() {
		HashSet<String> set=new HashSet<String>();
		set.add("womble");
		set.add("2008-99-99");
		return set;
	}

	public Set<String> getGoodParseData() {
		HashSet<String> set=new HashSet<String>();
		set.add("1965-12-12");
		set.add("2008-09-01");
		return set;
	}

	public Set<Date> getBadData() throws Exception {
		HashSet<Date> set = new HashSet<Date>();
		return set;
	}

	@SuppressWarnings("deprecation")
	public Set<Date> getGoodData() throws Exception {
		HashSet<Date> set = new HashSet<Date>();
		set.add(new Date(2008,10,06));
		return set;
	}

	@SuppressWarnings("unchecked")
	public I getInput() {
		return (I) new DateInput();
	}
   

}
