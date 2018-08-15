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

import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.inputs.ListInput;

/** An {@link InterfaceTest} for {@link ListInput}.
 * As factory classes override their default input to
 * not be a ListInput this is written to treat this case as an
 * automatic pass
 * 
 * @author spb
 *
 * @param <T>
 * @param <D>
 * @param <I>
 * @param <X>
 */

public interface ListInputInterfaceTest<T,D,I extends Input<T>&ListInput<T,D> , X extends TestDataProvider<T,I> >  {

	@Test
	public void testGetItembyValue() throws Exception ;
	
	@Test
	public void testGetItems() throws Exception; 
	@Test
	public void testGetTagByItem() throws Exception ;
	@Test
	public void testGetTagByValue() throws Exception;
	@Test
	public void testGetText() throws Exception ;
	
}