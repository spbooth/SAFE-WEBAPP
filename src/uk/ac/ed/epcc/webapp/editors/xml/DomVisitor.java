package uk.ac.ed.epcc.webapp.editors.xml;

import java.util.LinkedList;

import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public interface DomVisitor {

	/** first visit to an element.
	 * 
	 * @param e Element
	 * @param path  
	 * @return true if element should be recursed into
	 * @throws Exception 
	 */
	boolean beginStartElement(Element e, LinkedList<String> path) throws Exception;

	boolean useAttributes(Element e, LinkedList<String> path);

	void endStartElement(Element e, LinkedList<String> path);

	

	void endElement(Element e, LinkedList<String> path);

	void textNode(Text n, LinkedList<String> path);
	
	void commentNode(Comment c,LinkedList<String> path);

	void visitAttr(Attr a, LinkedList<String> path);

}
