package uk.ac.ed.epcc.webapp.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.model.TextFileOverlay.TextFile;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactoryTestCase;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;

public class TextFileOverlayTest extends DataObjectFactoryTestCase {

	

	@Override
	public DataObjectFactory getFactory() {
		return new TextFileOverlay(ctx);
	}

	@Test
	public void testFind() throws DataFault{
		TextFileOverlay over = (TextFileOverlay) getFactory();
		
		TextFile result = over.find("test_group", "test.txt");
		assertEquals("This is a test",result.getData());
		System.out.println(result.getURL());
		assertTrue(result.getURL().toString().endsWith("test.txt"));
	}
	
	@Test
	public void testSetBaseURL() throws DataFault, MalformedURLException{
		TextFileOverlay over = (TextFileOverlay) getFactory();
		over.setBaseURL(new URL("http://www.example.com"));
		TextFile result = over.find("test_group", "test.txt");
		assertEquals("This is a test",result.getData());
		System.out.println(result.getURL());
		assertTrue(result.getURL().toString().endsWith("test.txt"));
		assertEquals("http://www.example.com/test.txt",result.getURL().toString());
	}
	
	@Test
	public void testNoFile() throws DataFault{
		TextFileOverlay over = (TextFileOverlay) getFactory();
		
		TextFile result = over.find("test_group", "bogus.txt");
		assertTrue(result == null || ! result.hasData());
	}
}
