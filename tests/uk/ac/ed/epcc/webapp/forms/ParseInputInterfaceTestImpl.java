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
import uk.ac.ed.epcc.webapp.forms.inputs.OptionalInput;
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
		if( i instanceof OptionalInput ){
			((OptionalInput)i).setOptional(true);
		}
				
				
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
			i.validate();
			assertTrue("passed validate with expected fail "+text,expect);
		}catch(FieldException e){
			assertFalse("Exception thrown with "+text,expect);
		}
	
}
}
