package uk.ac.ed.epcc.webapp.editors.xml;

import java.util.LinkedList;

import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class AbstractDomVisitor implements DomVisitor {


	public boolean beginStartElement(Element e, LinkedList<String> path)
			throws Exception {
		return false;
	}

	
	public boolean useAttributes(Element e, LinkedList<String> path) {
		return false;
	}

	
	public void endStartElement(Element e, LinkedList<String> path) {
	}

	
	public void endElement(Element e, LinkedList<String> path) {


	}

	public void textNode(Text n, LinkedList<String> path) {


	}

	
	public void commentNode(Comment c, LinkedList<String> path) {


	}

	
	public void visitAttr(Attr a, LinkedList<String> path) {

	}

}
