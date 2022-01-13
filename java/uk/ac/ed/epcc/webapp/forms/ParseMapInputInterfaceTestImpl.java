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
import static org.junit.Assert.assertTrue;

import java.util.Map;




import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.inputs.ParseMapInput;


public  class ParseMapInputInterfaceTestImpl<T,I extends Input<T> & ParseMapInput,X extends TestDataProvider<T,I>> implements ParseMapInputInterfaceTest<T,I,X> {
	private X target;
	public ParseMapInputInterfaceTestImpl(X target){
		this.target=target;
	}
	
	public void testGoodDataParses() throws Exception{
		for(T dat : target.getGoodData()){
			I i =  target.getInput();
			i.setValue(dat);
			Map<String,Object> map = i.getMap();
			
			I j = target.getInput();
			j.parse(map);
			assertEquals(dat,i.getValue());
		}
	}
	
public  void checkValid(String text,boolean expect, I i) throws FieldException  {
		
		try{
			i.validate();
			assertTrue("passed validate with expected fail "+text,expect);
		}catch(FieldException e){
			assertFalse("Exception thrown with "+text,expect);
		}
	
}
}