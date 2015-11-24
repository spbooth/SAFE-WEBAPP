/*******************************************************************************
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.inputs.OptionalInput;



public interface OptionalInputInterfaceTest<T,I extends Input<T> & OptionalInput, X extends TestDataProvider<T,I>>  {

	@Test
	public void testIsOptional() throws Exception ;
	
}
