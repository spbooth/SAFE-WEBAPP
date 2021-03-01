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

import java.util.HashMap;

import javax.swing.JFrame;

import org.junit.Ignore;
import org.junit.Test;

import uk.ac.ed.epcc.webapp.ContextHolder;
import uk.ac.ed.epcc.webapp.content.HtmlBuilder;
import uk.ac.ed.epcc.webapp.forms.exceptions.FieldException;
import uk.ac.ed.epcc.webapp.forms.html.EmitHtmlInputVisitor;
import uk.ac.ed.epcc.webapp.forms.inputs.Input;
import uk.ac.ed.epcc.webapp.forms.inputs.TypeError;
import uk.ac.ed.epcc.webapp.forms.inputs.TypeException;
import uk.ac.ed.epcc.webapp.forms.swing.JFormDialog;
import uk.ac.ed.epcc.webapp.forms.swing.SwingContentBuilder;


@Ignore
public class InputInterfaceTestImpl<T,I extends Input<T>,X extends TestDataProvider<T,I>&ContextHolder> implements InputInterfaceTest<T,I,X>  {

	private X target;
	public InputInterfaceTestImpl(X target) {
		this.target=target;
	}

	@Override
	@Test
	public void testGetKey() throws Exception {
		I i = target.getInput();
		i.setKey("fred");
		assertEquals("fred", i.getKey());
	}
	@Override
	@Test
	public void testMakeHtml() throws Exception{
		// check for exceptions makeing html
		Input<T> input = target.getInput();
		input.setKey("test");
		HtmlBuilder hb = new HtmlBuilder();
		EmitHtmlInputVisitor vis = new EmitHtmlInputVisitor(null,false,hb, false, new HashMap() ,null,null);
		input.accept(vis);
	}
	
	@Override
	@Test
	public void testMakeSwing() throws Exception{
		if( target.getContext().getBooleanParameter("java.awt.headless",false)){
			// We can't run this test
			return;
		}
		JFrame frame = new JFrame();
		JFormDialog dialog = new JFormDialog(target.getContext(), frame);
		SwingContentBuilder cb = dialog.getContentBuilder();
		BaseForm form = new BaseForm(target.getContext());
		Field<T> f = new Field<>(form,"Test", "Test lable", target.getInput());
		cb.addFormLabel(target.getContext(), f);
		cb.addFormInput(target.getContext(), f, null);
	}
	
	@Override
	@Test
    public void testGood() throws  Exception{
    	
    	I in=target.getInput();
    	for(T dat : target.getGoodData()){
    		in.setValue(dat);
    		checkValid(dat,true, in);
    	}
    }
	@Override
	@Test
    public void testBad() throws Exception{
    	I in=target.getInput();
    	for(T dat : target.getBadData()){
    		try{
    			in.setValue(dat);
    			checkValid(dat,false, in);
    		}catch(TypeException e){
    			//Type error is acceptible response to bad data
    			// e.g. ConstantInput
    		}
    	}
    }
    public  void checkValid(T value,boolean expect, I i) throws FieldException  {
		
	
		try{
			if( ! i.isEmpty()) {
				i.validate();
				assertTrue("Exception validate passed for ["+value+"]",expect);
			}else {
				assertFalse("Inputs should only report empty when fail expected",expect);
			}
		}catch(FieldException e){
			assertFalse("Exception thrown for value "+value+" "+e.getMessage(), expect);
		}
	
   }
    
    @Override
	@Test
    public void testGetString() throws Exception{
    	I in=target.getInput();
    	for( T dat : target.getGoodData()){
    		String s = in.getString(dat);
    		String t = in.getPrettyString(dat);
    	}
    	
    	
    }

}