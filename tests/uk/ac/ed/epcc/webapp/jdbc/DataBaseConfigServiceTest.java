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
