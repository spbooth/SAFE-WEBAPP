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
