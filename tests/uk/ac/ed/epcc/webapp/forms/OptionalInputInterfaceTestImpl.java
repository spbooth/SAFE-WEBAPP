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
