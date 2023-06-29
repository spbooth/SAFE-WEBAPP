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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Locale;
import java.util.TreeMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import uk.ac.ed.epcc.webapp.AbstractContexed;
import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Feature;
import uk.ac.ed.epcc.webapp.config.ConfigService;
import uk.ac.ed.epcc.webapp.content.SimpleXMLBuilder;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.jdbc.table.DataBaseHandlerService;
import uk.ac.ed.epcc.webapp.model.data.Exceptions.DataFault;
import uk.ac.ed.epcc.webapp.timer.TimerService;

/** This class provides common boilerplate code for the
 * loading and creation of XML data dumps.
 * 
 * It is primarily intended for use in tests.
 * 
 * 
 * @author spb
 *
 */

public class XMLDataUtils extends AbstractContexed{
	/**
	 * 
	 */
	private static final Feature DROP_TABLES_FEATURE = new Feature("test.drop-tables", false,"drop all tables before test not just fixtures");
	private static final Feature MINIMAL_DIFF_FEATURE = new Feature("test.minimal-diff", false,"Only show changed fields in a diff");
	private XMLReader reader=null;
	private SAXParserFactory spf = SAXParserFactory.newInstance();
	/**
	 * 
	 */
	public XMLDataUtils(AppContext conn) {
		super(conn);
	}
	/** Read a set of fixture files.
	 * 
	 * All fixture files read by a single instance of {@link XMLDataUtils} will use the
	 * same {@link UnDumper} so back references may be made to previously loaded files.
	 * 
	 * @param clazz A Class to find resources relative to.
	 * @param fixtures
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws DataFault 
	 */
	public void readFixtures(Class clazz, String ... fixtures) throws ParserConfigurationException, SAXException, IOException, DataFault{
		AppContext c = getContext();
		XMLReader r = getXMLReader();
		TimerService serv = c.getService(TimerService.class);
		for( int i=0 ; i< fixtures.length ; i++){
			String fixture_name = c.expandText(fixtures[i]);
			//System.out.println("Loading "+fixture_name);
			if( serv != null ){
				serv.startTimer(fixtures[i]);
			}
			
			InputStream stm = clazz.getResourceAsStream(fixture_name);
			if( stm != null ){
				r.parse(new InputSource(stm));
			}else{
				throw new DataFault("Resource not found "+fixture_name);
			}
			if( serv != null ){
				serv.stopTimer(fixtures[i]);
			}
		}
		// we may have inserted properties into DB
		c.getService(ConfigService.class).clearServiceProperties();
		
	}
	/**
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	public XMLReader getXMLReader() throws ParserConfigurationException,
			SAXException {
		if( reader == null ){ 
			

			SAXParser parser = spf.newSAXParser();
			reader = parser.getXMLReader();
			reader.setContentHandler(new UnDumper(getContext()));
		}
		XMLReader r = reader;
		return r;
	}
	/** Just create tables from the fixtures
	 * 
	 * @param clazz
	 * @param fixtures
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws DataFault
	 */
	public void readTables(Class clazz, String ... fixtures) throws ParserConfigurationException, SAXException, IOException, DataFault{
		AppContext c = getContext();
		SAXParser parser = spf.newSAXParser();
		// we can make a new reader per call as there is no state to preserve
		XMLReader r = parser.getXMLReader();
		r.setContentHandler(new TableUnDumper(getContext()));
		
		TimerService serv = c.getService(TimerService.class);
		for( int i=0 ; i< fixtures.length ; i++){
			String fixture_name = c.expandText(fixtures[i]);
			//System.out.println("Loading "+fixture_name);
			if( serv != null ){
				serv.startTimer(fixtures[i]);
			}
			
			InputStream stm = clazz.getResourceAsStream(fixture_name);
			if( stm != null ){
				r.parse(new InputSource(stm));
			}else{
				throw new DataFault("Resource not found "+fixture_name);
			}
			if( serv != null ){
				serv.stopTimer(fixtures[i]);
			}
		}
	}
	public void getDiff(SimpleXMLBuilder output,InputSource baseline) throws DataException, Exception{
		Dumper dumper = new Dumper(getContext(), output);
		dumper.setVerboseDiff(! MINIMAL_DIFF_FEATURE.isEnabled(getContext()));
		DiffParser diff_parser = new DiffParser(getContext(), dumper);
		
		getDiff(diff_parser, baseline);
	}
	
	public void getDiff(DiffParser diff_parser,InputSource baseline) throws SAXException, DataFault, DataException, ConsistencyError, IOException, ParserConfigurationException{
		SAXParser parser = spf.newSAXParser();
		XMLReader r = parser.getXMLReader();
		r.setContentHandler(diff_parser);
		r.parse(baseline);
		dumpAllTables(diff_parser.getDumper());
	}
	
	
	/**
	 * @param dumper
	 * @throws DataException
	 * @throws DataFault
	 * @throws ConsistencyError
	 * @throws IOException 
	 */
	public void dumpAllTables(Dumper dumper) throws DataException, DataFault,
			ConsistencyError, IOException {
		DataBaseHandlerService handler = getContext().getService(DataBaseHandlerService.class);
		if( handler != null){
			// we want the order to be the same on all OS including windows that 
			// converts table names to lower case.
			TreeMap<String,String> map = new TreeMap<>();
			for(String name : handler.getTables()){
				map.put(name.toLowerCase(Locale.ENGLISH), name);
			}
			for( String key : map.keySet()){
				Repository res = Repository.getInstance(getContext(), Repository.TableToTag(getContext(), map.get(key)));
				dumper.dumpAll(res);
			}
		}
	}
	
	public void dumpAllSchema(Dumper dumper) throws DataException, DataFault,
	ConsistencyError, IOException {
		DataBaseHandlerService handler = getContext().getService(DataBaseHandlerService.class);
		if( handler != null){
			// we want the order to be the same on all OS including windows that 
			// converts table names to lower case.
			TreeMap<String,String> map = new TreeMap<>();
			for(String name : handler.getTables()){
				map.put(name.toLowerCase(Locale.ENGLISH), name);
			}
			for( String key : map.keySet()){
				Repository res = Repository.getInstance(getContext(), Repository.TableToTag(getContext(), map.get(key)));
				dumper.dumpSchema(res);
			}
		}
}
	/** read a resource as a String
	 * 
	 * @param clazz
	 * @param name
	 * @return
	 * @throws IOException
	 */
	public static String readResourceAsString(Class clazz, String name) throws IOException{
		StringBuffer fileData = new StringBuffer(1000);
		InputStream stream = clazz.getResourceAsStream(name);
		if( stream == null) {
			throw new IOException("Resource "+name+" not found by "+clazz.getCanonicalName());
		}
		InputStreamReader reader = new InputStreamReader(stream);
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		return fileData.toString().replace("\r\n", "\n");
	}
	/** read a resource as a {@link Source}
	 * 
	 * @param clazz
	 * @param name
	 * @return
	 */
	public static Source readResourceAsSource(Class clazz, String name){
		InputStream stream = clazz.getResourceAsStream(name);
		if( stream == null){
			throw new ConsistencyError("resource "+name+" cannot be found");
		}
		return new StreamSource(stream);
	}
	
	/** Perform a XSL transform.
	 * 
	 * @param t
	 * @param s
	 * @return
	 * @throws TransformerException
	 */
	public static String transform(Transformer t, Source s) throws TransformerException{
		StringWriter w = new StringWriter();
		StreamResult result = new StreamResult(w);
		t.transform(s, result);
		return w.toString();
	}
	
	/** Transform an XML document stored in a resource using a XSL transform
	 * 
	 * @param t
	 * @param clazz
	 * @param name
	 * @return
	 * @throws TransformerException
	 */
	public static String transform(Transformer t, Class clazz, String name) throws TransformerException{
		return transform(t, readResourceAsSource(clazz, name));
	}
	
	/** Transform an XML document stored in a string using a XSL transform
	 * 
	 * @param t
	 * @param data
	 * @return
	 * @throws TransformerException
	 */
	public static String transform(Transformer t, String data) throws TransformerException{
		return transform(t, new StreamSource(new StringReader(data)));
	}
	/** Drop ALL tables in the database. Not just those defined in the 
	 * fixtures
	 * 
	 */
	public void dropAllTables(){
		AppContext c = getContext();
		TimerService timer = c.getService(TimerService.class);
		if( timer != null ){ timer.startTimer("dropAllTables");}
		try{
			if( DROP_TABLES_FEATURE.isEnabled(c)){
				DataBaseHandlerService handler = c.getService(DataBaseHandlerService.class);
				
				if (DataBaseHandlerService.CLEAR_DATABASE.isEnabled(c)){
					// This is better if we have foreign keys
					handler.clearDatabase();
				}else{
					if( handler != null){
					for( String name : handler.getTables()){
						handler.deleteTable(name);
					}
					// we may have deleted the properties table
					c.getService(ConfigService.class).clearServiceProperties();
				}
				}
			}
		}catch(Exception t){
			getLogger().error("Error in dropAllTables",t);
		}finally{
			if( timer != null ){ timer.stopTimer("dropAllTables");}
		}
		
	}
}