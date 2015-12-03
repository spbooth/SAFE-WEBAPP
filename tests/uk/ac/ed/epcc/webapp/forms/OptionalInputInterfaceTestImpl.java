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

import java.util.Iterator;

import org.junit.Ignore;
import org.junit.Test;

import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.inputs.OptionalInput;


@Ignore
public class OptionalInputInterfaceTestImpl<T,I extends Input<T> & OptionalInput, X extends TestDataProvider<T,I>> implements OptionalInputInterfaceTest<T,I,X>  {
	
	private X target;
	public OptionalInputInterfaceTestImpl(X target) {
		this.target=target;
	}
	@Test
	public void testIsOptional() throws Exception {
		I i = target.getInput();
		i.setOptional(false);
		i.setValue(null);
		checkValid(false,i);
		i.setOptional(true);
		checkValid(true, i);
		Iterator<T> it = target.getGoodData().iterator();
		if( it .hasNext()){
		  i.setValue(it.next());
		  checkValid(true, i);
		  i.setOptional(false);
		  checkValid(true, i);
		}
	}
public  void checkValid(boolean expect, I i) throws FieldException  {
		try{
			i.validate();
			assertEquals(expect, true);
		}catch(FieldException e){
			assertEquals(expect, false);
		}
	
}
}