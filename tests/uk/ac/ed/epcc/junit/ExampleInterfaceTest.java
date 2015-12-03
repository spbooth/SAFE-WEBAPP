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