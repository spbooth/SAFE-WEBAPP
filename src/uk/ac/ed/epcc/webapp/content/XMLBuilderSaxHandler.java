//| Copyright - The University of Edinburgh 2011                            |
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
package uk.ac.ed.epcc.webapp.content;

import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/** A {@link ContentHandler} that forwards the events to 
 * a {@link SimpleXMLBuilder}. This is an adapter class to bridge between
 * the two interfaces
 * 
 * Optionally {@link XMLGenerator} objects can be included using XML processing instructions.
 * @author spb
 *
 */


public class XMLBuilderSaxHandler implements ContentHandler {

	/** Processing instruction used to include external content
	 * 
	 */
	public static final String EXTERNAL_CONTENT_PI = "external-content";
	private final SimpleXMLBuilder builder;
	private final Map<String,Object> data;
	public XMLBuilderSaxHandler(SimpleXMLBuilder builder){
		this.builder=builder;
		this.data=null;
	}
	public XMLBuilderSaxHandler(SimpleXMLBuilder builder,Map<String,Object> data){
		this.builder=builder;
		this.data=data;
	}
	public void characters(char[] arg0, int arg1, int arg2) throws SAXException {
		builder.clean(String.valueOf(arg0).subSequence(arg1, arg2));
	}

	public void endDocument() throws SAXException {

	}

	public void endElement(String arg0, String arg1, String arg2)
			throws SAXException {
		builder.close();

	}

	public void endPrefixMapping(String arg0) throws SAXException {

	}

	public void ignorableWhitespace(char[] arg0, int arg1, int arg2)
			throws SAXException {

	}

	public void processingInstruction(String arg0, String arg1)
			throws SAXException {
		if( data != null && arg0.equals(EXTERNAL_CONTENT_PI)){
			Object gen = data.get(arg1);
			if( gen != null && gen instanceof XMLGenerator){
				((XMLGenerator)gen).addContent(builder);
			}
		}
	}

	public void setDocumentLocator(Locator arg0) {
	}

	public void skippedEntity(String arg0) throws SAXException {

	}

	public void startDocument() throws SAXException {

	}

	public void startElement(String arg0, String arg1, String arg2,
			Attributes arg3) throws SAXException {
		builder.open(arg2);
		for(int i=0; i< arg3.getLength();i++){
			builder.attr(arg3.getLocalName(i), arg3.getValue(i));
		}

	}

	public void startPrefixMapping(String arg0, String arg1)
			throws SAXException {
		
	}

}