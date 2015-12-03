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