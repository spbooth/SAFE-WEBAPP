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
package uk.ac.ed.epcc.webapp.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.config.ConfigService;
import uk.ac.ed.epcc.webapp.junit4.DataBaseFixtures;


public class DataBaseConfigServiceTest extends WebappTestBase{
	
	
	@Test
	public void testDefaultService(){
		ConfigService serv = ctx.getService(ConfigService.class);
		//check we can get a tag from service props.
		assertEquals("simple",serv.getServiceProperties().getProperty("simple.tag"));
	}
	@Test
	public void testInclude(){
		ConfigService serv = ctx.getService(ConfigService.class);
		// check we see included files
		assertEquals("hello",serv.getServiceProperties().getProperty("extra.tag"));
	}
	
	@Test
	public void testOverride(){
		ConfigService serv = ctx.getService(ConfigService.class);
		// check that later files in an include list override earlier files
	    // but are not overridden by their children
		assertEquals("override",serv.getServiceProperties().getProperty("test.tag"));
		assertEquals("override",serv.getServiceProperties().getProperty("override.tag"));
	}
	@Test
	public void testNested(){
		ConfigService serv = ctx.getService(ConfigService.class);
		// check nesting works
		assertEquals("nested",serv.getServiceProperties().getProperty("nested.tag"));
	}
	@Test
	public void testSetProperties(){
		ConfigService serv = ctx.getService(ConfigService.class);
		
		assertNull(serv.getServiceProperties().getProperty("junk.prop",null));
		serv.setProperty("junk.prop", "ploog");
		assertEquals(serv.getServiceProperties().getProperty("junk.prop"), "ploog");
		
		
	}
	@Test
	@DataBaseFixtures({"Properties.xml"})
	public void testDatabaseProperty(){
 		
		ConfigService serv = ctx.getService(ConfigService.class);
		//check we can get a tag from database.
		assertEquals("exists",serv.getServiceProperties().getProperty("database.property"));

	}
}