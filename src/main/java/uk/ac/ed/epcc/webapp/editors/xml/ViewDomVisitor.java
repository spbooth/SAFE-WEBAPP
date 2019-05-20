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
package uk.ac.ed.epcc.webapp.editors.xml;

import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.content.ContentBuilder;
import uk.ac.ed.epcc.webapp.content.ExtendedXMLBuilder;
/** A {@link DomVisitor} that generates a clickable.
 * view of the selected document node.
 * 
 * @author spb
 *
 */
public class ViewDomVisitor implements DomVisitor {
	private ContentBuilder cb;
	private ExtendedXMLBuilder text;
	private AppContext conn;
	private Map<LinkedList<String>,Set<String>> notices=null;
	
	
	public ViewDomVisitor(AppContext c,ContentBuilder cb){
		this.cb=cb;
		this.conn=c;
	}
	public void setNotices(Map<LinkedList<String>,Set<String>> notices){
		this.notices=notices;
	}
	/** Tests to see if this element or any of its children
	 * have a notice set.
	 * 
	 * @param path
	 * @return
	 */
	private boolean childHasNotice(LinkedList<String> path){
		if( notices == null ){
			return false;
		}
		
		for(LinkedList<String> npath : notices.keySet()){
			if( npath.size() >= path.size()){
				boolean match = true;
				for(int i=0; i<path.size() && match ; i++){
					match = path.get(i).equals(npath.get(i));
				}
				if( match ){
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean beginStartElement(Element e, LinkedList<String> path) {
		
		XMLTarget node = DomTransitionProvider.makeXMLTarget(path, conn);
		
			cb=cb.getPanel("element");
			if( notices != null ){
				Set<String> s = notices.get(path);
				if( s != null && s.size() > 0){
					cb=cb.getPanel("xml_warn_scope");
					for(String w : s){
						cb=cb.getPanel("xml_warn");
						cb.addText(w);
						cb = cb.addParent();
					}
				}
			}
			cb=cb.getHeading(3);
			cb.addLink(conn, "<"+e.getNodeName(), new ViewXMLTargetResult(node));
			
			return true;
	}

	public boolean useAttributes(Element e, LinkedList<String> path) {
		return true;
	}

	
	public void endStartElement(Element e, LinkedList<String> path) {
		text=cb.getText();
		final boolean hasContent = e.hasChildNodes();
		if(! hasContent){
			text.clean("/");
		}
		text.clean(">");
		text.appendParent();
		text=null;
		cb=cb.addParent(); // heading
		if( hasContent ){
			cb=cb.getPanel("elementcontent");
		}
	}

	

	
	public void endElement(Element e, LinkedList<String> path) {
		if( e.hasChildNodes()){
			cb=cb.addParent();
			cb=cb.getHeading(3);
			text=cb.getText();
			text.clean("</");
			text.clean(e.getNodeName());
			text.clean(">");
			text.appendParent();
			text=null;
			cb=cb.addParent(); // heading
		}
		if( notices != null ){
			Set<String> s = notices.get(path);
			if( s != null ){
				cb=cb.addParent(); // warn scope
			}
		}
		cb=cb.addParent(); // div
	}

	
	public void textNode(Text n, LinkedList<String> path) {
//		text=cb.getText();
//		text.open("span");
//		text.attr("class", "xmltext");
//		text.clean(n.getTextContent());
//		text.close();
//		text.appendParent();
//		text=null;
		XMLTarget node = DomTransitionProvider.makeXMLTarget(path, conn);
		cb.addLink(conn, n.getTextContent(), new ViewXMLTargetResult(node));
	}
	
	public void commentNode(Comment n, LinkedList<String> path) {
		cb = cb.getPanel("xml_comment");
		cb.addText("<!--");
		XMLTarget node = DomTransitionProvider.makeXMLTarget(path, conn);
		cb.addLink(conn, n.getTextContent(), new ViewXMLTargetResult(node));
		cb.addText("-->");
		cb=cb.addParent();
	}
	
	public void visitAttr(Attr a, LinkedList<String> path) {
		cb.addText(" ");
		StringBuilder sb=new StringBuilder();
		sb.append(a.getNodeName());
		sb.append("=\"");
		sb.append(a.getValue());
		sb.append("\"");
		XMLTarget node = DomTransitionProvider.makeXMLTarget(path, conn);
		
		cb.addLink(conn, sb.toString(), new ViewXMLTargetResult(node));
	}

}