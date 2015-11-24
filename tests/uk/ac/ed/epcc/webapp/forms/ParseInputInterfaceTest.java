/*******************************************************************************
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms;

import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;
import uk.ac.ed.epcc.webapp.forms.inputs.OptionalInput;
import uk.ac.ed.epcc.webapp.forms.inputs.ParseInput;



public  interface ParseInputInterfaceTest<T,I extends ParseInput<T>,X extends TestParseDataProvider<T,I>>  {

	
	@Test
	public void parseNull() throws Exception;
	
	@Test
	public void testGoodDataParses() throws Exception;
	@Test
	public void testGoodParse() throws Exception;
	@Test
	public void testBadParse() throws Exception;

}
