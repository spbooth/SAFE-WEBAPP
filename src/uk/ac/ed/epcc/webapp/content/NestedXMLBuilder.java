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

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DocumentFragment;

import uk.ac.ed.epcc.webapp.Version;

/** A {@link SimpleXMLBuilder} that also implements {@link XMLGenerator} so XML content can be
 * added to a table.`
 * 
 * @author spb
 *
 */


public class NestedXMLBuilder extends XMLDomBuilder implements XMLGenerator{
	public static DocumentFragment makeFragment() throws ParserConfigurationException{
		return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument().createDocumentFragment();
	}
     public NestedXMLBuilder() throws ParserConfigurationException{
    	 super(makeFragment());
     }
	public SimpleXMLBuilder addContent(SimpleXMLBuilder builder) {
		DomWalker walker = new DomWalker(builder);
		walker.visit(getFragment().getChildNodes());
		return builder;
	}
}