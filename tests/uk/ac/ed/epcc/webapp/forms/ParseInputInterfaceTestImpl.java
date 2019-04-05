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

import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.exceptions.ParseException;

import uk.ac.ed.epcc.webapp.forms.inputs.ParseInput;



@Ignore
public  class ParseInputInterfaceTestImpl<T,I extends ParseInput<T>,X extends TestParseDataProvider<T,I>> implements ParseInputInterfaceTest<T,I,X>  {
	private X target;
	public ParseInputInterfaceTestImpl(X target){
		this.target=target;
	}
	
	@Test
	public void parseNull() throws Exception{
		I i = target.getInput();
		
				
				
		if( target.allowNull()){
			i.parse(null);
			assertNull(i.getValue());
		}
		
	}
	
	@Test
	public void testGoodDataParses() throws Exception{
		I i =  target.getInput();
		for(T dat : target.getGoodData()){
			String text = i.getString(dat);
			i.parse(text);
			checkValid(text,true,i);
			assertEquals(dat,i.getValue());
			assertEquals(i.getString(), text);
		}
	}
	@Test
	public void testGoodParse() throws Exception{
		I i =  target.getInput();
		Set<String> dat = target.getGoodParseData();
		if( dat == null ){
			return;
		}
		for(String text : dat ){
			i.parse(text);
			checkValid(text,true,i);
		}
	}
	@Test
	public void testBadParse() throws Exception{
		I i =  target.getInput();
		Set<String> dat = target.getBadParseData();
		if( dat == null ){
			return;
		}
		for(String text : dat){
			try{
			   i.parse(text);
			   checkValid(text,false,i);
	
			}catch(ParseException e){
				// ok expected this if error in parse
			}
		}
	}
public  void checkValid(String text,boolean expect, I i) throws FieldException  {
		
		try{
			// inputs that report empty are only checked for optional
			// validate is not called
			if( ! i.isEmpty()) {
				i.validate();
				assertTrue("passed validate with expected fail "+text,expect);
			}else {
				assertFalse("Empty inputs should be expected to fail ",expect);
			}
			
		}catch(FieldException e){
			assertFalse("Exception thrown with "+text,expect);
		}
	
}
}