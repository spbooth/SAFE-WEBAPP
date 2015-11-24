// Copyright - The University of Edinburgh 2011
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
@uk.ac.ed.epcc.webapp.Version("$Id: NestedXMLBuilder.java,v 1.2 2014/09/15 14:30:15 spb Exp $")

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