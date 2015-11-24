// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.model.data.stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;

/**
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.1 $")
public class MimeStreamDataTest {

	@Test
	public void setNameTest(){
		ByteArrayMimeStreamData msd = new ByteArrayMimeStreamData();
		msd.setName("fred");
		assertEquals("fred",msd.getName());
		
	}
	@Test
	public void setTypeTest(){
		ByteArrayMimeStreamData msd = new ByteArrayMimeStreamData();
		msd.setMimeType("text/plain");
		assertEquals("text/plain",msd.getContentType());
		
	}
	@Test
	public void setDataTest() throws DataFault{
		ByteArrayMimeStreamData msd = new ByteArrayMimeStreamData();
		PrintWriter pw = new PrintWriter(msd.getOutputStream());
		pw.print("hello world");
		pw.close();
		assertEquals("hello world",msd.toString());
		ByteArrayStreamData sd = new ByteArrayStreamData();
		sd.read(msd.getInputStream());
		
		assertEquals("hello world",sd.toString());
	}
	
	@Test
	public void testEquals() throws IOException, ClassNotFoundException{
		ByteArrayMimeStreamData msd = new ByteArrayMimeStreamData();
		msd.setName("hello.txt");
		msd.setMimeType("text/plain");
		PrintWriter pw = new PrintWriter(msd.getOutputStream());
		pw.print("hello world");
		pw.close();

		ByteArrayMimeStreamData msd2 = new ByteArrayMimeStreamData();
		msd2.setName("hello.txt");
		msd2.setMimeType("text/plain");
		PrintWriter pw2 = new PrintWriter(msd2.getOutputStream());
		pw2.print("hello world");
		pw2.close();
		assertEquals(msd,msd2);
	}
	
	@Test
	public void testSerialise() throws IOException, ClassNotFoundException{
		ByteArrayMimeStreamData msd = new ByteArrayMimeStreamData();
		msd.setName("hello.txt");
		msd.setMimeType("text/plain");
		PrintWriter pw = new PrintWriter(msd.getOutputStream());
		pw.print("hello world");
		pw.close();

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(stream);
		oos.writeObject(msd);
		oos.close();
		ByteArrayInputStream istream = new ByteArrayInputStream(stream.toByteArray());
		ObjectInputStream ois = new ObjectInputStream(istream);
		ByteArrayMimeStreamData msd2 = (ByteArrayMimeStreamData) ois.readObject();
		assertEquals(msd,msd2);
	}
}
