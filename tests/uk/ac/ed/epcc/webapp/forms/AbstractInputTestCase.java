/*******************************************************************************
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.forms.inputs.AbstractInput;
import uk.ac.ed.epcc.webapp.forms.inputs.TypeError;


public abstract class AbstractInputTestCase<T,I extends AbstractInput<T>> extends WebappTestBase implements TestDataProvider<T,I> ,
InputInterfaceTest<T, I, AbstractInputTestCase<T, I>>,
OptionalInputInterfaceTest<T, I, AbstractInputTestCase<T, I>>
{
	

	public InputInterfaceTest<T, I, AbstractInputTestCase<T, I>> input_test = new InputInterfaceTestImpl<T, I, AbstractInputTestCase<T,I>>(this);
	

	public OptionalInputInterfaceTest<T, I, AbstractInputTestCase<T, I>> optional_input_test = new OptionalInputInterfaceTestImpl<T, I, AbstractInputTestCase<T,I>>(this);


	@Override
	@Test
	public final void testIsOptional() throws Exception {
		optional_input_test.testIsOptional();
	}


	@Override
	@Test
	public final void testGetKey() throws Exception {
		input_test.testGetKey();
		
	}


	@Override
	@Test
	public final void testMakeHtml() throws Exception {
		input_test.testMakeHtml();
	}


	@Override
	@Test
	public final void testMakeSwing() throws Exception {
		input_test.testMakeSwing();
		
	}


	@Override
	@Test
	public final void testGood() throws TypeError, Exception {
		input_test.testGood();
		
	}


	@Override
	@Test
	public final void testBad() throws Exception {
		input_test.testBad();
	}


	@Override
	@Test
	public final void testGetString() throws Exception {
		input_test.testGetString();
	}

	
	
	
	
}
