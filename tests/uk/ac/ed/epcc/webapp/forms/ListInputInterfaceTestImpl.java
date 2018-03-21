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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;



import uk.ac.ed.epcc.webapp.forms.inputs.AlternateInput;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.inputs.ListInput;

/** An  InterfaceTest for {@link ListInput}.
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

public class ListInputInterfaceTestImpl<T,D,I extends Input<T> , X extends TestDataProvider<T,I> >  implements ListInputInterfaceTest<T,D,I,X>{

	private X target;
	public ListInputInterfaceTestImpl(X target) {
		this.target=target;
	}
	
	public void testGetItembyValue() throws Exception {
		ListInput<T, D> input = getInput();
		if( input == null) return;
		
		for(T val :target.getGoodData() ){
			D item = input.getItembyValue(val);
			assertNotNull(item);
		}
		
	}
	/** Locate an appropriate input to test
	 * @return
	 * @throws Exception
	 */
	public ListInput<T, D> getInput() throws Exception {
		I input = target.getInput();
		
		if( input instanceof ListInput){
			return (ListInput<T, D>) input;
		}
		if( input instanceof AlternateInput){
			AlternateInput comp = (AlternateInput) input;
			for(Iterator<Input> it=comp.getInputs(); it.hasNext();){
				Input i = it.next();
				if( i instanceof ListInput){
					return (ListInput<T, D>) i;
				}
			}
		}
		return null;
	}
	
	public void testGetItems() throws Exception {
		Set<D> s = new HashSet<D>();
		ListInput<T, D> input = getInput();
		if( input == null) return;
		for(Iterator<D> it = input.getItems(); it.hasNext();){
			D item = it.next();
			assertNotNull(item);
			assertFalse(s.contains(item));
			s.add(item);
		}
		// check can set to null
		input.setItem(null);
		assertNull(input.getItem());
		assertNull(input.getValue());
		if( target.getInput() instanceof ListInput){
			// If this is an alternate input things not in the
			// item set might be valid
			for(T val :target.getGoodData() ){
				D item = input.getItembyValue(val);
				assertTrue("Not in expected set "+item,s.contains(item));
			}
		}
		for(T val :target.getBadData() ){
			try{
			D item = input.getItembyValue(val);
			if( item != null ){
				assertFalse(s.contains(item));
			}
			}catch(Exception e){
				// bad value might throw excpetion instead
			}
		}
		
		
	}

	public void testGetTagByItem() throws Exception {
		ListInput<T, D> input = getInput();
		if( input == null) return;
		
		for(T val : target.getGoodData() ){
			D item = input.getItembyValue(val);
			assertNotNull(item);
			String tag=input.getTagByItem(item);
			assertNotNull(tag);
			assertTrue(tag.length() > 0);
			assertEquals(tag, input.getTagByValue(val));
		}
		
	}
	
	public void testGetTagByValue() throws Exception {
		ListInput<T, D> input = getInput();
		if( input == null) return;
		Set<String> s = new HashSet<String>();
		for(Iterator<D> it = input.getItems(); it.hasNext();){
			D item = it.next();
			assertNotNull(item);
			String tag = input.getTagByItem(item);
			assertNotNull(tag);
			assertTrue(tag.length() > 0);
			assertFalse(s.contains(tag));
			s.add(tag);
			input.setItem(item);
			T val = input.getValue();
			assertEquals(tag, input.getTagByValue(val));
		}
		
	}
	
	public void testGetText() throws Exception {
		ListInput<T, D> input = getInput();
		if( input == null) return;
		Set<String> s = new HashSet<String>();
		for(Iterator<D> it = input.getItems(); it.hasNext();){
			D item = it.next();
			assertNotNull(item);
			String text = input.getText(item);
			assertNotNull(text);
			assertTrue(text.length() > 0);
			assertFalse("Duplicate label "+text,s.contains(text));
			s.add(text);
		}
		
		assertNull(input.getText(null));
		
	}
	
}