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
/*
 * Created on 24-Aug-2004
 *
 */
package uk.ac.ed.epcc.webapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.Hashtable;

import org.junit.Before;
import org.junit.Test;
import java.util.Properties;
import java.util.Date;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.config.CachedConfigService;
import uk.ac.ed.epcc.webapp.jdbc.config.DataBaseConfigService;


/**
 * @author spb
 *
 */
public class AppContextTest {
    AppContext ctx;

	@Before
	public void setUp() throws Exception {
		ClassLoader cl = this.getClass().getClassLoader();
		InputStream service_props_stream = cl.getResourceAsStream("test.properties");
		Properties test_prop = new Properties();
		Properties properties = System.getProperties();
		test_prop.load(service_props_stream);
		// merge unset props, existing valeus in the system properties take precidence
		for( Object name : test_prop.keySet() ){
			if( ! properties.containsKey(name)){
				properties.setProperty(name.toString(), test_prop.getProperty(name.toString()));
			}
		}
		//System.setProperty("saf.defaults","WEB-INF/tests/test.properties");
		ctx = new AppContext();
	}

	@Test
	public void testSimpleAppContext() {
		AppContext c = new AppContext();
		assertNotNull(c);
	}
	
	@Test
	public void testGetInitParameter() {
		
		AppContext c = new AppContext();
		String value = c.getInitParameter("test.value");
		assertEquals(value,"found it!");
	}
	
	@Test
	public void testGetExpandedParameter(){
		AppContext c = new AppContext();
		String value = c.getExpandedProperty("test.expand.value");
		assertEquals(value,"Have I found it! yet?");
	}
	
	@Test
	public void testGetDoubleExpandedParameter(){
		AppContext c = new AppContext();
		String value = c.getExpandedProperty("test.double.expand.value");
		assertEquals(value,"Have I found it! yet? found it!");
	}
	@Test
	public void testGetTripleExpandedParameter(){
		AppContext c = new AppContext();
		String value = c.getExpandedProperty("test.triple.expand.value");
		assertEquals(value,"[Have I found it! yet? found it!]");
	}
	@Test
	public void testGetRecursiveParameter(){
		AppContext c = new AppContext();
		String value = c.getExpandedProperty("test.recursive");
		assertEquals(value,"I eat myself .");
	}
	@Test
	public void testExpandText(){
		AppContext c = new AppContext();
		String value = c.expandText("A ${test.value} B ${test.value} C");
		assertEquals(value,"A found it! B found it! C");
		
		assertEquals("hello world", c.expandText("hello world"));
	}
	@Test
	public void testGetInitParameters(){
		AppContext c = new AppContext();
		String value = c.getInitParameter("test.value");
		assertEquals(value,"found it!");
		Hashtable h = c.getInitParameters("");
		assertTrue(h.size() > 0);
		assertTrue(h.containsKey("test.value"));
		assertEquals(h.get("test.value"),"found it!");
			
	}
	
	@Test
    public void testDataBaseConfig() throws Exception{
    	AppContext c = new AppContext();
    	c.setService( new DataBaseConfigService(c));
		c.setService( new CachedConfigService(c));
		String value = c.getInitParameter("test.value");
		assertEquals(value,"found it!");

    }
	
	@Test
	public void testAttributes() {
		AppContext c = new AppContext();
		assertFalse(c.hasAttribute("Thing"));
		assertFalse(c.hasAttribute("Null"));
		c.setAttribute("Thing", "Goober");
		c.setAttribute("Null", null);
		assertTrue(c.hasAttribute("Thing"));
		assertTrue(c.hasAttribute("Null"));
		assertEquals("Goober", c.getAttribute("Thing"));
		assertNull(c.getAttribute("Null"));
		
	}
	@Test
	public void testDateProperty() {
		AppContext c = new AppContext();
		
		Date d = c.getDateParameter("test.date_prop", null);
		assertNotNull(d);
		assertEquals(new Date(65,11,12,10,30,0),d);
	}
	
}