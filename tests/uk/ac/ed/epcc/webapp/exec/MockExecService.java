//| Copyright - The University of Edinburgh 2017                            |
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
package uk.ac.ed.epcc.webapp.exec;

import java.io.StringReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.content.XMLBuilderSaxHandler;
import uk.ac.ed.epcc.webapp.content.XMLPrinter;

/**
 * @author spb
 *
 */
public class MockExecService  implements ExecService,Contexed {

	
	
	private final class MockProcessProxy implements DeferredProcessProxy{

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.exec.ProcessProxy#getOutput()
		 */
		@Override
		public String getOutput() {
			return "";
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.exec.ProcessProxy#getErr()
		 */
		@Override
		public String getErr() {
			return "";
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.exec.ProcessProxy#getExit()
		 */
		@Override
		public Integer getExit() {
			return Integer.valueOf(0);
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.exec.ProcessProxy#wasTerminated()
		 */
		@Override
		public boolean wasTerminated() {
			return false;
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.exec.DeferredProcessProxy#execute(long)
		 */
		@Override
		public Integer execute(long timeout_millis) throws Exception {
			return getExit();
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.exec.DeferredProcessProxy#start()
		 */
		@Override
		public void start() {
			
			
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.exec.DeferredProcessProxy#complete(long)
		 */
		@Override
		public void complete(long timeout_millis) throws InterruptedException {
			
			
		}

		/* (non-Javadoc)
		 * @see uk.ac.ed.epcc.webapp.exec.DeferredProcessProxy#setTimeout(long)
		 */
		@Override
		public void setTimeout(long timeout) {
			
		}
		
	}

	private final XMLPrinter printer;
	private final AppContext conn;
	private boolean expect_xml=false;
	/**
	 * 
	 */
	public MockExecService(AppContext conn) {
		this.conn=conn;
		printer = new XMLPrinter();
		printer.open("Operations");
	}

	public String getState() {
		printer.close();
		String result = printer.toString();
		printer.clear();
		printer.open("Operations");
		return result;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.AppContextService#cleanup()
	 */
	@Override
	public void cleanup() {
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.AppContextService#getType()
	 */
	@Override
	public Class<? super ExecService> getType() {
		return ExecService.class;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.exec.ExecService#exec(long, java.lang.String)
	 */
	@Override
	public ProcessProxy exec(long timeout_milliseconds, String command) throws Exception {
		return exec(null,timeout_milliseconds,command);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.exec.ExecService#exec(java.lang.String, long, java.lang.String)
	 */
	@Override
	public ProcessProxy exec(String input, long timeout_milliseconds, String command) throws Exception {
		DeferredProcessProxy proxy = exec_deferred(input==null? null : input.getBytes(), command);
		proxy.execute(timeout_milliseconds);
		return proxy;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Contexed#getContext()
	 */
	@Override
	public AppContext getContext() {
		return conn;
	}

	/**
	 * @return the expect_xml
	 */
	public boolean expectXml() {
		return expect_xml;
	}

	/**
	 * @param expect_xml the expect_xml to set
	 */
	public void setXml(boolean expect_xml) {
		this.expect_xml = expect_xml;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.exec.ExecService#exec_deferred(java.lang.String, java.lang.String)
	 */
	@Override
	public DeferredProcessProxy exec_deferred(byte input[], String command) throws Exception {
		printer.open("Exec");
		printer.open("Command");
		printer.clean(command);
		printer.close();
		if( input != null) {
			printer.open("Input");
			if(expect_xml) {
				XMLBuilderSaxHandler handler = new XMLBuilderSaxHandler(printer);
				StringReader inputStream = new StringReader(new String(input));
				InputSource source = new InputSource(inputStream);
				
				SAXParserFactory spf = SAXParserFactory.newInstance();
		
				SAXParser parser = spf.newSAXParser();
				XMLReader reader = parser.getXMLReader();
				reader.setContentHandler(handler);
				reader.parse(source);
			}else {
				printer.clean(new String(input));
			}
		}
		printer.close();
		printer.close();
		return new MockProcessProxy();
	}

}
