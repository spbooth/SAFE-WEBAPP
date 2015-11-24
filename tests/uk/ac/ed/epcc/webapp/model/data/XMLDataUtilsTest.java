// Copyright - The University of Edinburgh 2014
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
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.4 $")
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
		 assertEquals("<Test id='1'>\n<Name>fred</Name>\n<Number>19998.0</Number>\n<UnsignedInt>0</UnsignedInt>\n<Mandatory>Junk</Mandatory>\n</Test>\n",diff.toString());
		
	}
	
}
