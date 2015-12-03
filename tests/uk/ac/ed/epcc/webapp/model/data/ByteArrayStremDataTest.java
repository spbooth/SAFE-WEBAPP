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
package uk.ac.ed.epcc.webapp.model.data;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.stream.ByteArrayStreamData;
import uk.ac.ed.epcc.webapp.model.data.stream.StreamData;


public class ByteArrayStremDataTest extends WebappTestBase {

	
	@Test
	public void testInsert() throws  IOException{
		StreamData s = new ByteArrayStreamData();
		String message = "hello world !!!";
		
		OutputStream o = s.getOutputStream();
		
		o.write(message.getBytes());
		o.close();
		InputStream i = s.getInputStream();
		StringWriter sw = new StringWriter();
		int c;
		while( (c = i.read()) >= 0){
		   sw.write(c);	
		}
		assertEquals(message, sw.toString());
	}
	
	@Test
	public void testArrayInsert() throws DataFault, IOException{
		String message = "hello world !!!";
		StreamData s = new ByteArrayStreamData(message.getBytes());
		
		
		
		InputStream i = s.getInputStream();
		StringWriter sw = new StringWriter();
		int c;
		while( (c = i.read()) >= 0){
		   sw.write(c);	
		}
		assertEquals(message, sw.toString());
	}
	
	@Test
	public void testdoubleInsert() throws  IOException{
		StreamData s = new ByteArrayStreamData();
		String message = "hello world !!!";
		String message2 = "goodbye cruel world!";
		OutputStream o = s.getOutputStream();
		
		o.write(message.getBytes());
		o.close();
		InputStream i = s.getInputStream();
        o = s.getOutputStream();
		
		o.write(message2.getBytes());
		o.close();
		InputStream i2 = s.getInputStream();
		StringWriter sw = new StringWriter();
		int c;
		while( (c = i.read()) >= 0){
		   sw.write(c);	
		}
		assertEquals(message, sw.toString());
		
		sw = new StringWriter();
		while( (c = i2.read()) >= 0){
		   sw.write(c);	
		}
		assertEquals(message2, sw.toString());
	}
}