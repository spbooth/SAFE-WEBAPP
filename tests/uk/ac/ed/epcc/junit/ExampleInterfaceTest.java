/*******************************************************************************
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.junit;

import org.junit.Test;



/** This demonstrates the interface test pattern.
 * 
 * We define an interface for the tests we expect the test class to implement and an associated 
 * composite implementation class that holds the logic.
 * To keep things within the normal junit idiom we need to fill out forwarding methods.
 * 
 * @author spb
 *
 * @param <X>
 */
public interface ExampleInterfaceTest<X extends ExampleInterface> extends TargetProvider<X>{

	
	@Test
	public void testDoAdd() ;
	@Test
	public void testDoThrow() throws Exception;
}
