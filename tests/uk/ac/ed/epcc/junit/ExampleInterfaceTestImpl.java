/*******************************************************************************
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Ignore;

@Ignore
public class ExampleInterfaceTestImpl<X extends TargetProvider<ExampleInterface>>  {

	private X target_provider;
	public ExampleInterfaceTestImpl(X target ) {
		this.target_provider=target;
	}
	
	public void testDoAdd() {
		TargetProvider<ExampleInterface> t = target_provider;
		ExampleInterface i = t.getTarget();
		
		assertEquals(5, i.doAdd(1, 4));
	}

	public void testDoThrow() throws Exception{
		TargetProvider<ExampleInterface> t = target_provider;
		ExampleInterface i = t.getTarget();
		i.doThrow(false);
		try{
			i.doThrow(true);
			assertFalse("should never get here",true);
		}catch(Exception e){
			//  ok expect this
		}
		
	}
}
