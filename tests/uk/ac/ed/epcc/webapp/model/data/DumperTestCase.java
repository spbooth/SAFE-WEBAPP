package uk.ac.ed.epcc.webapp.model.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import uk.ac.ed.epcc.webapp.WebappTestBase;
import uk.ac.ed.epcc.webapp.content.XMLPrinter;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.model.Dummy1;
import uk.ac.ed.epcc.webapp.model.Dummy2;
import uk.ac.ed.epcc.webapp.model.LinkDummy;
import uk.ac.ed.epcc.webapp.model.LinkDummy.DummyLink;
public class DumperTestCase extends WebappTestBase {
	
	String test1_opnly ="<Dump>\n"+
			"<TableSpecification name='Test'>\n"+
			"<Name type='String' nullable='true' max='32'/>\n"+
			"<Number type='Double' nullable='true'/>\n"+
			"<UnsignedInt type='Long' nullable='true'/>\n"+
			"<Time type='Long' nullable='true'/>\n"+
			"</TableSpecification>\n" +
			"<Test id='1'>\n" +
			"<Name>fred</Name>\n" +
			"<Number>5.0</Number>\n" +
			"<UnsignedInt>0</UnsignedInt>\n"+
			"</Test>\n"+
			"<Test id='2'>\n" +
			"<Name>igor</Name>\n" +
			"<Number>99.0</Number>\n" +
			"<UnsignedInt>7</UnsignedInt>\n"+
			"</Test>\n"+
			"</Dump>";
	
	String expect ="<Dump>\n"+
	"<TableSpecification name='LinkTest1Test2'>\n"+
	"<Test1ID reference='Test'/>\n"+
	"<Test2ID reference='Test2'/>\n"+
	"<Status type='String' nullable='false' max='1'/>\n"+
	"<Link type='Index' unique='true'><Column name='Test1ID'/><Column name='Test2ID'/></Link>\n"+
	"<LeftKey type='Index' unique='false'><Column name='Test1ID'/></LeftKey>\n"+
	"<RightKey type='Index' unique='false'><Column name='Test2ID'/></RightKey>\n"+
	"</TableSpecification>\n"+
	"<TableSpecification name='Test'>\n"+
	"<Name type='String' nullable='true' max='32'/>\n"+
	"<Number type='Double' nullable='true'/>\n"+
	"<UnsignedInt type='Long' nullable='true'/>\n"+
	"<Mandatory type='String' nullable='false' max='32'/>\n"+
	"<Time type='Long' nullable='true'/>\n"+
	"</TableSpecification>\n" +
	"<Test id='1'>\n" +
	"<Name>fred</Name>\n" +
	"<Number>5.0</Number>\n" +
	"<UnsignedInt>0</UnsignedInt>\n"+
	"<Mandatory>Junk</Mandatory>\n"+
	"</Test>\n"+
	"<TableSpecification name='Test2'>\n" +
	"<Name type='String' nullable='true' max='32'/>\n" +
	"<Number type='Double' nullable='true'/>\n" +
	"</TableSpecification>\n"+
	"<Test2 id='1'>\n" +
	"<Name>boris</Name>\n" +
	"<Number>8.0</Number>\n" +
	"</Test2>\n"+
	"<LinkTest1Test2 id='1'>\n" +
	"<Test1ID>1</Test1ID>\n" +
	"<Test2ID>1</Test2ID>\n" +
	"<Status>I</Status>\n" +
	"</LinkTest1Test2>\n"+
	"</Dump>";

	@Test
	public void testDump() throws Exception{
		
		LinkDummy linker;
		Dummy1.Factory d1_fac;
		Dummy2.Factory d2_fac;
	    linker = LinkDummy.getInstance(ctx);
	    d1_fac = new Dummy1.Factory(ctx);
	    d2_fac = new Dummy2.Factory(ctx);
	    
		Dummy1 d1 = new Dummy1(ctx);
		d1.setName("fred");
		d1.setNumber(5);
		d1.commit();
		Dummy2 d2 = new Dummy2(ctx);
		d2.setName("boris");
		d2.setNumber(8);
		d2.commit();
		
		assertFalse(linker.isLinked(d1,d2));
		linker.addLink(d1,d2);
		assertTrue(linker.isLinked(d1,d2));
		linker.removeLink(d1,d2);
		assertFalse(linker.isLinked(d1,d2));
		DummyLink l = linker.getLink(d1,d2);
		assertEquals(d1,l.getDummy1());
		assertEquals(d2,l.getDummy2());
		XMLPrinter printer = new XMLPrinter();
		printer.open("Dump");
		printer.clean("\n");
		Dumper dumper = new Dumper(ctx, printer);
		
		for( LinkDummy.DummyLink link : linker.all()){
			dumper.dump(link.record);
		}
		printer.close();
		
		final String result = printer.toString();
		System.out.println(result);
	
		assertEquals("Unexpected result", expect, result);
	}
	@Test
	public void testUnDump() throws IOException, SAXException, ParserConfigurationException, DataException{
		LinkDummy linker;
		Dummy1.Factory d1_fac;
		Dummy2.Factory d2_fac;
	    linker = LinkDummy.getInstance(ctx);
	    d1_fac = new Dummy1.Factory(ctx);
	    d2_fac = new Dummy2.Factory(ctx);
		
		SAXParserFactory spf = SAXParserFactory.newInstance();
		
		SAXParser parser = spf.newSAXParser();
		XMLReader reader = parser.getXMLReader();
		reader.setContentHandler(new UnDumper(ctx));
		reader.parse(new InputSource(new StringReader(expect)));
		
		Dummy1 fred = d1_fac.find(d1_fac.new StringFilter("fred"));
		assertNotNull("Fred not found", fred);
		Dummy2 boris = d2_fac.find(d2_fac.new StringFilter("boris"));
		assertNotNull("Boris not found", boris);
		assertNotNull(linker.getLink(fred, boris));
		
	}
	
	@Test
	public void testReverse() throws IOException, SAXException, ParserConfigurationException, DataException{
		
		// create tables in undump then redump to see if recreated correctly
		SAXParserFactory spf = SAXParserFactory.newInstance();
		
		SAXParser parser = spf.newSAXParser();
		XMLReader reader = parser.getXMLReader();
		reader.setContentHandler(new UnDumper(ctx));
		reader.parse(new InputSource(new StringReader(expect)));
		LinkDummy linker;
		linker = LinkDummy.getInstance(ctx);
		XMLPrinter printer = new XMLPrinter();
		printer.open("Dump");
		printer.clean("\n");
		Dumper dumper = new Dumper(ctx, printer);

		for( LinkDummy.DummyLink link : linker.all()){
			dumper.dump(link.record);
		}
		printer.close();

		final String result = printer.toString();
		System.out.println(result);

		assertEquals("Unexpected result", expect, result);
	}
	
	@Test
	public void testUnDumpWithMerge() throws IOException, SAXException, ParserConfigurationException, DataException{
		
	    
	    UnDumper handler = new UnDumper(ctx);

		SAXParserFactory spf = SAXParserFactory.newInstance();
		
		SAXParser parser = spf.newSAXParser();
		
		XMLReader reader = parser.getXMLReader();
		reader.setContentHandler(handler);
		reader.parse(new InputSource(new StringReader(test1_opnly)));
		
		reader = parser.getXMLReader();
		reader.setContentHandler(handler);
		reader.parse(new InputSource(new StringReader(expect)));
		
		LinkDummy linker;
		Dummy1.Factory d1_fac;
		Dummy2.Factory d2_fac;
	    linker = LinkDummy.getInstance(ctx);
	    d1_fac = new Dummy1.Factory(ctx);
	    d2_fac = new Dummy2.Factory(ctx);
		Dummy1 fred = d1_fac.find(d1_fac.new StringFilter("fred"));
		assertNotNull("Fred not found", fred);
		Dummy1 igor = d1_fac.find(d1_fac.new StringFilter("igor"));
		assertNotNull("Igor not found", fred);
		assertEquals(2, d1_fac.getCount(null));
		Dummy2 boris = d2_fac.find(d2_fac.new StringFilter("boris"));
		assertNotNull("Boris not found", boris);
		assertNotNull(linker.getLink(fred, boris));
		assertEquals(1, d2_fac.getCount(null));
	}
	
	@Test
	public void testUnDumpWithExisting() throws IOException, SAXException, ParserConfigurationException, DataException{
		LinkDummy linker;
		Dummy1.Factory d1_fac;
		Dummy2.Factory d2_fac;
	    linker = LinkDummy.getInstance(ctx);
	    d1_fac = new Dummy1.Factory(ctx);
	    d2_fac = new Dummy2.Factory(ctx);
		Dummy1 d1 = new Dummy1(ctx);
		d1.setName("agnes");
		d1.setNumber(18);
		d1.commit();
		
		SAXParserFactory spf = SAXParserFactory.newInstance();
		
		SAXParser parser = spf.newSAXParser();
		XMLReader reader = parser.getXMLReader();
		// Have to map ids when merging into existing data
		UnDumper d = new UnDumper(ctx,true);
		d.setPreserveIds(false);
		reader.setContentHandler(d);
		reader.parse(new InputSource(new StringReader(expect)));
		Dummy1 fred = d1_fac.find(d1_fac.new StringFilter("fred"));
		assertNotNull("Fred not found", fred);
		assertEquals(2, d1_fac.getCount(null));
		Dummy2 boris = d2_fac.find(d2_fac.new StringFilter("boris"));
		assertNotNull("Boris not found", boris);
		assertNotNull(linker.getLink(fred, boris));
		assertEquals(1, d2_fac.getCount(null));
	}

	
}
