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


import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/** Class to walk a DOM Tree and map content to a SimpleXMLBuilder
 * 
 * @author spb
 *
 */


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