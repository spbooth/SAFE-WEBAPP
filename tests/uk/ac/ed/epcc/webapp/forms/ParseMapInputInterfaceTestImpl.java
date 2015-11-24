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
