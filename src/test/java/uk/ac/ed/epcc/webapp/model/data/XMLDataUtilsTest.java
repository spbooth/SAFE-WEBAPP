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
package uk.ac.ed.epcc.webapp.model.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;


import java.io.StringReader;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.content.XMLPrinter;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.model.Dummy1;
import uk.ac.ed.epcc.webapp.model.Dummy2;
import uk.ac.ed.epcc.webapp.model.LinkDummy;

/**
 * @author spb
 *
 */

public class XMLDataUtilsTest extends WebappTestBase {

	/**
	 * 
	 */
	public XMLDataUtilsTest() {
	}

	
	@Test
	public void testUndump() throws ParserConfigurationException, SAXException, IOException, DataException{
		XMLDataUtils utils = new XMLDataUtils(getContext());
		
		utils.readFixtures(getClass(), "test.xml");
		
		LinkDummy linker;
		Dummy1.Factory d1_fac;
		Dummy2.Factory d2_fac;
	    linker = LinkDummy.getInstance(ctx);
	    d1_fac = new Dummy1.Factory(ctx);
	    d2_fac = new Dummy2.Factory(ctx);
	    Dummy1 fred = d1_fac.find(d1_fac.new StringFilter("fred"));
		assertNotNull("Fred not found", fred);
		Dummy2 boris = d2_fac.find(d2_fac.new StringFilter("boris"));
		assertNotNull("Boris not found", boris);
		assertNotNull(linker.getLink(fred, boris));
	}
	
	
	@Test
	public void testDiff() throws DataException, Exception{
		
		XMLDataUtils utils = new XMLDataUtils(getContext());
		
		utils.readFixtures(getClass(), "test.xml");
		
		// make a baseline
		XMLPrinter baseline = new XMLPrinter();
		baseline.open("Data");
		utils.dumpAllTables(new Dumper(getContext(), baseline));
		baseline.close();
	    System.out.println(baseline.toString());
	    XMLPrinter diff = new XMLPrinter();	
	    StringReader reader = new StringReader(baseline.toString());
		utils.getDiff(diff, new InputSource(reader));
		assertEquals("",diff.toString());
		Dummy1.Factory d1_fac;
		d1_fac = new Dummy1.Factory(ctx);
		 Dummy1 fred = d1_fac.find(d1_fac.new StringFilter("fred"));
		 fred.setNumber(19998);
		 fred.commit();
		 reader = new StringReader(baseline.toString());
		 utils.getDiff(diff, new InputSource(reader));
		 System.out.println(diff.toString());
		 assertEquals("<Test id='1'>\n<Number>19998.0</Number>\n</Test>\n",diff.toString());
		
	}
	
}