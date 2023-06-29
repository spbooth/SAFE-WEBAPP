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
package uk.ac.ed.epcc.webapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.util.Calendar;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamSource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.RuleChain;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import uk.ac.ed.epcc.webapp.config.ConfigService;
import uk.ac.ed.epcc.webapp.content.SimpleXMLBuilder;
import uk.ac.ed.epcc.webapp.content.XMLPrinter;
import uk.ac.ed.epcc.webapp.content.XMLWriter;
import uk.ac.ed.epcc.webapp.email.Emailer;
import uk.ac.ed.epcc.webapp.email.MockTansport;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.table.DataBaseHandlerService;
import uk.ac.ed.epcc.webapp.junit4.AppContextFixtureRule;
import uk.ac.ed.epcc.webapp.junit4.DBFixtureRule;
import uk.ac.ed.epcc.webapp.logging.Logger;
import uk.ac.ed.epcc.webapp.logging.LoggerService;
import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;
import uk.ac.ed.epcc.webapp.model.data.Dumper;
import uk.ac.ed.epcc.webapp.model.data.UnDumper;
import uk.ac.ed.epcc.webapp.model.data.XMLDataUtils;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.model.data.stream.ByteArrayStreamData;
import uk.ac.ed.epcc.webapp.timer.TimerService;

/** A base class for Junit4 tests that require an {@link AppContext}
 * 
 * @author spb
 *
 */
public abstract class WebappTestBase implements ContextHolder{
	protected AppContext ctx;
	protected Logger log;
	
	// Need appcontext first
	@Rule
	public RuleChain chain = RuleChain.outerRule(new AppContextFixtureRule(this)).around(new DBFixtureRule(this));
	
	
	
	public void setContext(AppContext c){
		this.ctx=c;
		this.log = c.getService(LoggerService.class).getLogger(getClass());
	}
	public AppContext getContext(){
		return ctx;
	}
	/** Method to dump the current state of the database to an XML file
	 * 
	 * @param name
	 */
	protected void save(String name){
		try{
			Logger log = ctx.getService(LoggerService.class).getLogger(getClass());
			String dir = getClass().getCanonicalName();
			dir = dir.substring(0,dir.lastIndexOf("."));
			dir = dir.replace('.', '/');
			dir = "src/test/resources/"+dir;
			File output = new File(dir+"/"+name+".xml");
			FileWriter w = new FileWriter(output);
			SimpleXMLBuilder builder = new XMLWriter(w);
			builder.open("dataset");
			Dumper d = new Dumper(ctx, builder);
			DataBaseHandlerService serv = ctx.getService(DataBaseHandlerService.class);
			for(String tab : serv.getTables()){
				try{
					DataObjectFactory<? extends DataObject> fac = ctx.makeObject(DataObjectFactory.class, tab);
					if( fac != null ) {
						for(DataObject o: fac.all()){
							d.dump(o);
						}
					}else {
						log.warn("No factory for "+tab);
					}
				}catch(Exception t){
					log.warn("Error in dump of "+tab, t);
				}
			}
			builder.close();
			builder.appendParent();
			//w.close();
		}catch(Exception t){
			ctx.error("Error dumping fixtures",t);
		}

	}
	/** Safe the contents of the specified factory. Directory is generated from the package of the
	 * test running the method.
	 * 
	 * @param prefix  directory prefix (ie the source dir)
	 * @param name    file basename
	 * @param fac     factory to dump
	 */
	protected void save(String prefix,String name,DataObjectFactory<? extends DataObject> fac){
		try{
			Logger log = ctx.getService(LoggerService.class).getLogger(getClass());
			String dir = getClass().getCanonicalName();
			dir = dir.substring(0,dir.lastIndexOf("."));
			dir = dir.replace('.', '/');
			dir = prefix+"/"+dir;
			File output = new File(dir+"/"+name+".xml");
			FileWriter w = new FileWriter(output);
			SimpleXMLBuilder builder = new XMLWriter(w);
			builder.open("dataset");
			Dumper d = new Dumper(ctx, builder);
			for(DataObject o: fac.all()){
				d.dump(o);
			}

			builder.close();
			builder.appendParent();
			//w.close();
		}catch(Exception t){
			ctx.error("Error dumping fixtures",t);
		}

	}
	/** method to load a XML database dump.
	 * This can be used from a "Before" method to load
	 * data applicable to all test methods in a class.
	 * 
	 * @param name
	 * @throws Exception
	 */
	protected void load(String name)throws Exception{
		SAXParserFactory spf = SAXParserFactory.newInstance();

		SAXParser parser = spf.newSAXParser();
		XMLReader reader = parser.getXMLReader();
		reader.setContentHandler(new UnDumper(ctx));


		InputStream stm = getClass().getResourceAsStream(name);
		if( stm != null ){
			reader.parse(new InputSource(stm));
		}

		// we may have inserted properties into DB
		ctx.getService(ConfigService.class).clearServiceProperties();

	}
	
	public String readFileAsString(String name) throws IOException {
		StringBuffer fileData = new StringBuffer(1000);
		InputStream stream = getClass().getResourceAsStream(name);
		if( stream == null ){
			return null;
		}
		try(Reader reader = new InputStreamReader(stream)){
			char[] buf = new char[1024];
			int numRead = 0;
			while ((numRead = reader.read(buf)) != -1) {
				String readData = String.valueOf(buf, 0, numRead);
				fileData.append(readData);
				buf = new char[1024];
			}
		}
		return fileData.toString();
		
	}
	@Before
	public void setupXML(){
		utils = new XMLDataUtils(ctx);
	}
	
	protected  XMLDataUtils utils;
	protected  XMLPrinter baseline = new XMLPrinter();
	  
	/** take and remember a baseline dump of the current database state
	 * that will be used in a subsequent call to {{@link #checkDiff(String, String)}
	 * 
	 * @throws DataFault
	 * @throws DataException
	 * @throws ConsistencyError
	 * @throws IOException 
	 */
	public final void takeBaseline() throws DataFault, DataException, ConsistencyError, IOException{
		
		// make a baseline dump
		baseline.clear();
		baseline.open("Data");
		utils.dumpAllTables(new Dumper(getContext(), baseline));
		baseline.close();
	}
	
	/** Diff the current database state against the baseline (as XML) and compare with an expected 
	 * XML stored in a resource.
	 * 
	 * The expected and generated XML are run through a XSL transform that should normalise any time-dependent
	 * values.
	 * 
	 * @param normalize_transform
	 * @param expected_xml
	 * @throws DataException
	 * @throws Exception
	 */
	public final void checkDiff(String normalize_transform,String expected_xml) throws DataException, Exception{
		XMLPrinter diff = new XMLPrinter();	
		TimerService timer = ctx.getService(TimerService.class);
		if( timer !=null){ timer.startTimer("diff"); }
		diff.open("Diff");
	    StringReader reader = new StringReader(baseline.toString());
		utils.getDiff(diff, new InputSource(reader));
		diff.close();
		//System.out.println(diff.toString());
		
		String expected;
		String raw=diff.toString();
		String result;
		expected_xml = ctx.expandText(expected_xml);
		if( normalize_transform != null ) {
			
			String expected_text = XMLDataUtils.readResourceAsString(getClass(),expected_xml);
			// Do a shortcut test in case the two are identical
			if( expected_text.trim().equals(raw.trim())) {
				System.out.println("@@@@@@ Shortcut Diff @@@@@@");
				return;
			}
			
			
			//This is a XSL transform to edit the dates in the project log as these will depend on the time the test is run.
			TransformerFactory tfac = TransformerFactory.newInstance();
			
			Source source = XMLDataUtils.readResourceAsSource(getClass(), normalize_transform);
			if( timer !=null){ timer.stopTimer("diff"); }
			assertNotNull(source);
			Transformer t = tfac.newTransformer(source);
			assertNotNull(t);

			
			expected = XMLDataUtils.transform(t,new StreamSource(new StringReader(expected_text)));
			result = XMLDataUtils.transform(t, raw);
		}else {
			result = raw.trim();
			expected = XMLDataUtils.readResourceAsString(getClass(), expected_xml);
			expected.trim();
			// Do a shortcut test in case the two are identical
			if( expected.equals(result)) {
				System.out.println("@@@@@@ Shortcut Diff @@@@@@");
				return;
			}
		}
		//System.out.println(result);
		String differ = TestDataHelper.diff(expected, result);
		boolean same = differ.trim().length()==0;
		if( ! same){
		
			//FileWriter w = new FileWriter(expected_xml);
			//w.write(raw);
			//w.close();
			
			
			System.out.println("Got: "+result);
			System.out.println("--------------------------------------------------------------------");
			System.out.println("Expected: "+expected);
			System.out.println("====================================================================");
			System.out.println("Raw:");
			System.out.println(raw);
		}
		assertEquals("Unexpected result:"+expected_xml+"\n"+differ,expected,result);
	}
	/** Check database has not changed.
	 * 
	 * @throws DataException
	 * @throws Exception
	 */
	public final void checkUnchanged() throws DataException, Exception{
		XMLPrinter diff = new XMLPrinter();	
		
		diff.open("Diff");
	    StringReader reader = new StringReader(baseline.toString());
		utils.getDiff(diff, new InputSource(reader));
		diff.close();
		//System.out.println(diff.toString());
		
		assertEquals("No change","<Diff/>", diff.toString());
	}
	/** Save a diff file as part of test development.
	 * Make sure so read the contents and check it looks correct before committing.
	 * 
	 * @param file_name
	 * @throws Exception 
	 * @throws DataException 
	 */
	public void saveDiff(String file_name) throws DataException, Exception{
		XMLPrinter diff = new XMLPrinter();	
		
		diff.open("Diff");
	    StringReader reader = new StringReader(baseline.toString());
		utils.getDiff(diff, new InputSource(reader));
		diff.close();
		String string = diff.toString();
		
		writeFile(file_name, string);
	}
	/** Save a diff file as part of test development.
	 * Make sure so read the contents and check it looks correct before committing.
	 * 
	 * @param file_name
	 * @throws Exception 
	 * @throws DataException 
	 */
	public void saveSchema(String file_name) throws DataException, Exception{
		XMLPrinter schema = new XMLPrinter();	
		
		schema.open("Tables");
	 
		utils.dumpAllSchema(new Dumper(getContext(), schema));
		schema.close();
		String string = schema.toString();
		
		writeFile(file_name, string);
	}
	protected void writeFile(String file_name, String string) throws FileNotFoundException {
		
		PrintWriter writer = new PrintWriter(new File(file_name));
		writer.println(string);
		writer.close();
	}
protected void writeFile(String file_name, byte data[]) throws IOException {
		
	OutputStream stream = new FileOutputStream(new File(file_name));
	stream.write(data);
	stream.close();
	}
	public String getResourceAsString(String name) throws IOException{
		InputStream stream = getClass().getResourceAsStream(getContext().expandText(name));
		StringBuffer fileData = new StringBuffer(1000);
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(stream))){
			char[] buf = new char[1024];
			int numRead = 0;
			while ((numRead = reader.read(buf)) != -1) {
				String readData = String.valueOf(buf, 0, numRead);
				fileData.append(readData);
				buf = new char[1024];
			}
		}
		return fileData.toString();
	}
	public byte[] getResourceAsBytes(String name) throws IOException, DataFault{
		InputStream stream = getResourceAsStream(name);
		ByteArrayStreamData data = new ByteArrayStreamData();
		data.read(stream);
		return data.getBytes();
	}
	protected InputStream getResourceAsStream(String name) {
		return getClass().getResourceAsStream(getContext().expandText(name));
	}
	
	/** Check the contents of some test generated XML contenet
	 * @param normalize_transform
	 * @param expected_xml
	 * @param content
	 * @throws TransformerFactoryConfigurationError
	 * @throws TransformerConfigurationException
	 * @throws TransformerException
	 * @throws IOException 
	 */
	public void checkContent(String normalize_transform, String expected_xml, String content)
			throws TransformerFactoryConfigurationError, TransformerConfigurationException, TransformerException, IOException {
		
		String expected_text = XMLDataUtils.readResourceAsString(getClass(), expected_xml);
		// shortcut test
		if( content.trim().equals(expected_text.trim())) {
			System.out.println("@@@@ Shortcut checkContent @@@@@@");
			return;
		}
		
		TransformerFactory tfac = TransformerFactory.newInstance();
		 Transformer tt;
		 if( normalize_transform == null ){
			 normalize_transform="/normalize.xsl";
		 }
			 Source source = XMLDataUtils.readResourceAsSource(getClass(), normalize_transform);
			 assertNotNull(source);
			 tt = tfac.newTransformer(source);
		 
		 assertNotNull(tt);
		 
		String result = XMLDataUtils.transform(tt, content);
		
		 String expected = XMLDataUtils.transform(tt,new StreamSource(new StringReader(expected_text)));
		 
		 String differ = TestDataHelper.diff(expected, result);
		 boolean same = differ.trim().length()==0;
		 if( ! same ){
			 System.out.println(content);
		 }
		assertEquals("Unexpected result:"+expected_xml+"\n"+differ,expected,result);
	}
	@Before
	public void clearEmails() {
		MockTansport.clear();
		Emailer.resetReport();
	}
	/**
	 * 
	 */
	public void deferredEmails() {
		// Run any deferred email sends
		CleanupService serv = ctx.getService(CleanupService.class);
		if( serv != null) {
			serv.action(Emailer.SendAction.class);
		}
	}
	public void deferredActions() {
		// Run any deferred email sends
		CleanupService serv = ctx.getService(CleanupService.class);
		if( serv != null) {
			serv.action();
		}
	}

	/** Convenience function to set the test-time via a {@link TestTimeService}
	 *  
	 * @param year
	 * @param month
	 * @param day
	 * @param hour
	 * @param min
	 * @return Date set by method
	 */
    public Date setTime(int year, int month, int day, int hour, int min) {
    	return setTime(year, month, day, hour, min, 0);
    }
    public Date setTime(int year, int month, int day, int hour, int min, int sec) {
    	
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(year, month, day, hour, min,sec);
		return setTime(cal);
    }
   public Date setTime(Calendar cal) {
		TestTimeService serv = new TestTimeService();
		serv.setResult(cal.getTime());
		ctx.setService(serv);
		return cal.getTime();
    }
   
   public void checkImageEqual(String expected,byte actual[]) throws IOException {
	   checkImageEqual(expected, ImageIO.read(new ByteArrayInputStream(actual)));
   }
   public void checkImageEqual(String expected,BufferedImage actual) throws IOException {
	   checkImageEqual(ImageIO.read(getResourceAsStream(expected)), actual);
   }
   public void checkImageEqual(BufferedImage expected, BufferedImage actual) {
	   // compare images pixel by pixel. Comparing the binary file representation
	   // is not as good as this depends on the library implementation and has been
	   // known to change over major java versio updates
	   int ew = expected.getWidth();
	   int aw = actual.getWidth();
	   assertEquals("Image Width", ew, aw);
	   int eh = expected.getHeight();
	   int ah = actual.getHeight();
	   assertEquals("Image Height", eh, ah);
	   for( int x=0; x< ew; x++ ) {
		   for( int y=0; y < eh ; y++) {
			   assertEquals("Image("+x+","+y+")",expected.getRGB(x, y),actual.getRGB(x, y));
		   }
	   }
   }
}