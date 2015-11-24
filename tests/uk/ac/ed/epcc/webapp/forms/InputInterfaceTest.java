/*******************************************************************************
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.forms;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.ContextHolder;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.inputs.TypeError;


public interface InputInterfaceTest<T,I extends Input<T>,X extends TestDataProvider<T,I>&ContextHolder>  {

	@Test
	public void testGetKey() throws Exception;
	@Test
	public void testMakeHtml() throws Exception;
	
	@Test
	public void testMakeSwing() throws Exception;
	@Test
    public void testGood() throws TypeError, Exception;
	@Test
    public void testBad() throws Exception;
   
    
    @Test
    public void testGetString() throws Exception;

}
