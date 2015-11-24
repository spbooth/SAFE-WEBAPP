/*******************************************************************************
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms;

import org.junit.Test;
import org.junit.runner.RunWith;

import uk.ac.ed.epcc.webapp.forms.inputs.ParseAbstractInput;
import uk.ac.ed.epcc.webapp.forms.inputs.TypeError;

/**
 * 
 * @author spb
 *
 * @param <T> Type returned by input
 * @param <I> Type of input
 */
public abstract class ParseAbstractInputTestCase<T,I extends ParseAbstractInput<T>> extends AbstractInputTestCase<T,I> implements TestParseDataProvider<T,I>,
ParseInputInterfaceTest<T, I, ParseAbstractInputTestCase<T, I>>{

	
	public ParseInputInterfaceTest<T, I, ParseAbstractInputTestCase<T, I>> parse_input_test = new ParseInputInterfaceTestImpl<T, I, ParseAbstractInputTestCase<T,I>>(this);
	
	public boolean allowNull(){
		return true;
	}
	@Override
	@Test
	public final void parseNull() throws Exception {
		parse_input_test.parseNull();
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.ParseInputInterfaceTest#testGoodDataParses()
	 */
	@Override
	@Test
	public final void testGoodDataParses() throws Exception {
		parse_input_test.testGoodDataParses();
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.ParseInputInterfaceTest#testGoodParse()
	 */
	@Override
	@Test
	public final void testGoodParse() throws Exception {
		parse_input_test.testGoodParse();
		
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.forms.ParseInputInterfaceTest#testBadParse()
	 */
	@Override
	@Test
	public final void testBadParse() throws Exception {
		parse_input_test.testBadParse();
		
	}
	
	
}
