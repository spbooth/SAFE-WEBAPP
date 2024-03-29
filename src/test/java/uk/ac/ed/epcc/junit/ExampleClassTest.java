//| Copyright - The University of Edinburgh 2015                            |
//|                                                                         |
//| Licensed under the Apache License, Version 2.0 (the "License");         |
//| you may not use this file except in compliance with the License.        |
//| You may obtain a copy of the License at                                 |
//|                                                                         |
//|    http://www.apache.org/licenses/LICENSE-2.0                           |
//|                                                                         |
//| Unless required by applicable law or agreed to in writing, software     |
//| distributed under the License is distributed on an "AS IS" BASIS,       |
//| WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.|
//| See the License for the specific language governing permissions and     |
//| limitations under the License.                                          |
/*******************************************************************************
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.junit;

import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.ContextHolder;
import uk.ac.ed.epcc.webapp.junit4.AppContextFixtureRule;
import uk.ac.ed.epcc.webapp.junit4.DBFixtureRule;

public class ExampleClassTest implements ContextHolder,ExampleInterfaceTest<ExampleInterface>{
	
	public ExampleInterfaceTestImpl<ExampleClassTest> example_interface = new ExampleInterfaceTestImpl<>(this);
	private  AppContext c;
	
	// Need appcontext first
	@Rule
	public RuleChain chain = RuleChain.outerRule(new AppContextFixtureRule(this)).around(new DBFixtureRule(this));
		
	
	
	@Override
	public  ExampleClass getTarget(){
		return new ExampleClass();
	}
	@org.junit.Test
	public void testDoSubtract() {
		ExampleClass t = getTarget();
		
		assertEquals(5, t.doSubtract(10, 5));
		
	}
	
	@Override
	public void setContext(AppContext c){
		this.c=c;
	}
	
	@Override
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