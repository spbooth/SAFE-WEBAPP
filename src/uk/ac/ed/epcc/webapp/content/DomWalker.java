// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp.content;


import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import uk.ac.ed.epcc.webapp.Version;

/** Class to walk a DOM Tree and map content to a SimpleXMLBuilder
 * 
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: DomWalker.java,v 1.2 2014/09/15 14:30:14 spb Exp $")

public class DomWalker {
	SimpleXMLBuilder builder;
	public DomWalker(SimpleXMLBuilder builder){
		this.builder=builder;
	}
	public void visit(Node n){
		if( n.getNodeType() == Node.ELEMENT_NODE){
			visit((Element)n);
			return;
		}else if( n.getNodeType() == Node.TEXT_NODE){
			visit((Text)n);
		}
	}
	
	public void visit(Element e){
		builder.open(e.getTagName());
		NamedNodeMap attr = e.getAttributes();
	    for(int i=0; i<attr.getLength(); i++){
	    	Attr a = (Attr) attr.item(i);
	    	builder.attr(a.getName(), a.getValue());
	    }
	   NodeList children = e.getChildNodes();
	   visit(children);
	    
	    
	    builder.close();
		
	}
	public void visit(NodeList children) {
		for(int i=0 ; i< children.getLength(); i++){
			   visit(children.item(i));
		   }
	}
	public void visit(Text t){
		builder.clean(t.getTextContent());
	}
}