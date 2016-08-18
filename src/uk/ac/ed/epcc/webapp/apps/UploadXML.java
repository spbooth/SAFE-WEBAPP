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
package uk.ac.ed.epcc.webapp.apps;

import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.util.LinkedList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.forms.exceptions.ActionException;
import uk.ac.ed.epcc.webapp.model.data.UnDumper;

/**
 * @author spb
 *
 */
public class UploadXML implements Command {

	private final AppContext conn;
	/**
	 * 
	 */
	public UploadXML(AppContext conn) {
		this.conn=conn;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.Contexed#getContext()
	 */
	@Override
	public AppContext getContext() {
		return conn;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.apps.Command#run(java.util.LinkedList)
	 */
	@Override
	public void run(LinkedList<String> args) {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		for(String name : args){
			File f = new File(name);
			if( ! f.canRead()){
				CommandLauncher.die("Cannot read "+name);
				return;
			}
			UnDumper undumper = new UnDumper(getContext());
			
			try {
				SAXParser parser = spf.newSAXParser();
				XMLReader reader = parser.getXMLReader();
				reader.setContentHandler(undumper);
				reader.parse(new InputSource(new FileInputStream(f)));
			} catch (Exception e) {
				CommandLauncher.die(e);
			}
		}

	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.apps.Command#description()
	 */
	@Override
	public String description() {
		return "Load XML dumps into the database";
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.apps.Command#help()
	 */
	@Override
	public String help() {
		// TODO Auto-generated method stub
		return "list of file-names";
	}

}
