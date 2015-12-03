//| Copyright - The University of Edinburgh 2014                            |
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