//| Copyright - The University of Edinburgh 2016                            |
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
package uk.ac.ed.epcc.webapp.model.far;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;

/**
 * @author spb
 * @param <O> 
 * @param <F> 
 *
 */
public class XMLFormReader<O extends PartOwner,F extends PartOwnerFactory<O>> implements Contexed {

	private final AppContext conn;
	private SAXParserFactory spf = SAXParserFactory.newInstance();
	/**
	 * @param conn 
	 * 
	 */
	public XMLFormReader(AppContext conn) {
		this.conn=conn;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Contexed#getContext()
	 */
	@Override
	public AppContext getContext() {
		return conn;
	}
	
	public void read(F fac, O owner, String data) throws ParserConfigurationException, SAXException, IOException{
		SAXParser parser = spf.newSAXParser();
		XMLReader reader = parser.getXMLReader();
		reader.setContentHandler(new XMLFormParser(conn, fac, owner));
		reader.parse(new InputSource(new StringReader(data)));
	}

}
