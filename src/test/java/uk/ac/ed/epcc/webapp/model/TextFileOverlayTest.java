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