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

import org.junit.Test;



/** This demonstrates the interface test pattern.
 * 
 * We define an interface for the tests we expect the test class to implement and an associated 
 * composite implementation class that holds the logic.
 * To keep things within the normal junit idiom we need to fill out forwarding methods.
 * 
 * Unfortunately junit (at least when run from eclipse) does not pick up tests purely as default methods though they
 * can be used in implementation class. This is supposed to work in junit5 where interface tests can be implemented as interfaces
 * 
 * @author spb
 *
 * @param <X>
 */
public interface ExampleInterfaceTest<X extends ExampleInterface> extends TargetProvider<X>{

	/** This is an example of an interface test written as a default method.
	 * In Junit4 we still need to forward the actual test method onto the implementation 
	 * class to have the test run and a default method just ensures that forgetting to do this
	 * is a silent error. 
	 * 
	 */
	@Test
	default public void testDoAdd(){
		
		ExampleInterface i = getTarget();
		
		assertEquals(5, i.doAdd(1, 4));
	}
	@Test
	public void testDoThrow() throws Exception;
}