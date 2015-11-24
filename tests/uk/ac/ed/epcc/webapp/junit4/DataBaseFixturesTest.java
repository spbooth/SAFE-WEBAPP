// Copyright - The University of Edinburgh 2014
package uk.ac.ed.epcc.webapp.junit4;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.jdbc.table.DataBaseHandlerService;
import uk.ac.ed.epcc.webapp.model.data.XMLDataUtils;

/**
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.2 $")
@DataBaseFixtures("CreateTable3.xml")
public class DataBaseFixturesTest extends WebappTestBase {

	/**
	 * 
	 */
	public DataBaseFixturesTest() {
		
	}
	
	
	@Test
	@DataBaseFixtures({"CreateTable.xml","CreateTable2.xml"})
	public void testFixtures(){
		DataBaseHandlerService serv = ctx.getService(DataBaseHandlerService.class);
		
		assertTrue(serv.tableExists("Test"));
		assertTrue(serv.tableExists("Test2"));
	}

	@Test
	@DataBaseFixtures({"${test.fixture_name}.xml","${test.fixture_name}2.xml"})
	public void testFixtureExpansion(){
		DataBaseHandlerService serv = ctx.getService(DataBaseHandlerService.class);
		
		assertTrue(serv.tableExists("Test"));
		assertTrue(serv.tableExists("Test2"));
	}
	@Test
	public void testGlobalFixture(){
		DataBaseHandlerService serv = ctx.getService(DataBaseHandlerService.class);
		assertFalse(serv.tableExists("Test"));
		assertFalse(serv.tableExists("Test2"));
		assertTrue(serv.tableExists("Test3"));
		
	}
	
	@Test
	@DataBaseFixtures({"CreateTable.xml","CreateTable2.xml"})
	public void testGlobalAndLocalFixtures(){
		DataBaseHandlerService serv = ctx.getService(DataBaseHandlerService.class);
		
		assertTrue(serv.tableExists("Test"));
		assertTrue(serv.tableExists("Test2"));
		assertTrue(serv.tableExists("Test3"));
	}
}
