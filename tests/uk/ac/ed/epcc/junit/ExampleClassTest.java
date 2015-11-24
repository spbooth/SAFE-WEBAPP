/*******************************************************************************
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.junit;

import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.ContextHolder;
import uk.ac.ed.epcc.webapp.junit4.AppContextFixtureRule;
import uk.ac.ed.epcc.webapp.junit4.DBFixtureRule;

public class ExampleClassTest implements ContextHolder,ExampleInterfaceTest<ExampleInterface>{
	
	public ExampleInterfaceTestImpl<ExampleClassTest> example_interface = new ExampleInterfaceTestImpl<ExampleClassTest>(this);
	private  AppContext c;
	
	@Rule
	public DBFixtureRule db = new DBFixtureRule(this);
	
	@Rule
	public AppContextFixtureRule afr = new AppContextFixtureRule(this);
	
	
	public  ExampleClass getTarget(){
		return new ExampleClass();
	}
	@org.junit.Test
	public void testDoSubtract() {
		ExampleClass t = getTarget();
		
		assertEquals(5, t.doSubtract(10, 5));
		
	}
	
	public void setContext(AppContext c){
		this.c=c;
	}
	
	public AppContext getContext() {
		return c;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.junit.ExampleInterfaceTest#testDoAdd()
	 */
	@Override
	@Test
	public void testDoAdd() {
		example_interface.testDoAdd();
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.junit.ExampleInterfaceTest#testDoThrow()
	 */
	@Override
	@Test
	public void testDoThrow() throws Exception {
		example_interface.testDoThrow();
		
	}
	

}
