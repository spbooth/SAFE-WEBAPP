/*******************************************************************************
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.forms.inputs.DayMultiInput;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.inputs.TypeError;



public class DayMultiInputTest extends MultiInputTestBase<Date,Input<Integer>,DayMultiInput> implements TestParseDataProvider<Date, DayMultiInput> ,

ParseInputInterfaceTest<Date, DayMultiInput, DayMultiInputTest>
{


 
	public ParseInputInterfaceTest<Date, DayMultiInput, DayMultiInputTest> parse_input_test = new ParseInputInterfaceTestImpl<Date, DayMultiInput, DayMultiInputTest>(this);
	
	public DayMultiInput getInput() throws Exception {
		return  new DayMultiInput();
	}
   
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.model.data.forms.DateInputTest#getGoodParseData()
	 */
	public Set<String> getGoodParseData() {
		
		Set<String> res = new HashSet<String>();
		res.add("Now+0d");
		res.add("Now-1y");
		res.add("\nNow-1m\n");
		res.add("2010-12-12");
		return res;
	}
	public Set<Date> getGoodData() throws Exception {
		Set<Date> res = new HashSet<Date>();
		return res;
	}
	public Set<Date> getBadData() throws Exception {
		Set<Date> res = new HashSet<Date>();
		return res;
	}
	public Set<String> getBadParseData() {
		Set<String> res = new HashSet<String>();
		res.add("womble");
		return res;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.TestParseDataProvider#allowNull()
	 */
	public boolean allowNull() {
		return true;
	}
	
	@Override
	@Test
	public void parseNull() throws Exception {
		parse_input_test.parseNull();
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.ParseInputInterfaceTest#testGoodDataParses()
	 */
	@Override
	@Test
	public void testGoodDataParses() throws Exception {
		parse_input_test.testGoodDataParses();
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.ParseInputInterfaceTest#testGoodParse()
	 */
	@Override
	@Test
	public void testGoodParse() throws Exception {
		parse_input_test.testGoodParse();
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.ParseInputInterfaceTest#testBadParse()
	 */
	@Override
	@Test
	public void testBadParse() throws Exception {
		parse_input_test.testBadParse();
		
	}
	
}
