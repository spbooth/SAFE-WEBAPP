// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.content;

import java.util.Stack;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import uk.ac.ed.epcc.webapp.Version;
import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;

/** A SimpleXMLBuilder that modifies a DocumentFragment
 * 
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: XMLDomBuilder.java,v 1.4 2014/10/21 15:39:05 spb Exp $")

public class XMLDomBuilder implements SimpleXMLBuilder {
    private final DocumentFragment frag;
    private Stack<Element> stack;
    private String ns=null;
    private XMLDomBuilder parent=null;
    public XMLDomBuilder(DocumentFragment fragment){
    	frag=fragment;
    	stack = new Stack<Element>();
    }
    public void setNameSpace(String ns){
    	this.ns=ns;
    }
    public DocumentFragment getFragment(){
    	return frag;
    }
	public SimpleXMLBuilder clean(CharSequence s) {
		addString(s.toString());
		return this;
	}
	public void addString(String s){
		Document doc = frag.getOwnerDocument();
		Text text = doc.createTextNode(s);
		if( stack.isEmpty()){
			frag.appendChild(text);
		}else{
			stack.peek().appendChild(text);
		}
	}

	public SimpleXMLBuilder clean(char c) {
		addString(""+c);
		return this;
	}

	public SimpleXMLBuilder clean(Number i) {
		addString(i.toString());
		return this;
	}

	public SimpleXMLBuilder close() {
		if( stack.isEmpty()){
			  throw new ConsistencyError("no matching open tag");
		  }
		stack.pop();
		return this;
	}

	public SimpleXMLBuilder open(String tag) {
		open(tag,null);
		return this;
	}

	public SimpleXMLBuilder open(String tag, String[][] attr) {
		Element e;
		Document doc = frag.getOwnerDocument();
		if( ns != null ){
			e = doc.createElementNS(ns, tag);
		}else{
			e=doc.createElement(tag);
		}
		if(stack.empty()){
			frag.appendChild(e);
		}else{
			Element head = stack.peek();
			head.appendChild(e);
		}
		stack.push(e);
		if( attr != null && attr.length > 0){
			for(int i=0;i<attr.length;i++){
				String name=attr[i][0];
				String value=attr[i][1];
				e.setAttribute(name, value);
			}
		}
		return this;
	}
	public SimpleXMLBuilder attr(String name, CharSequence s) {
		Element e = stack.peek();
		e.setAttribute(name, s.toString());
		return this;
	}
	public SimpleXMLBuilder getNested() throws UnsupportedOperationException {
		XMLDomBuilder nested = new XMLDomBuilder(frag.getOwnerDocument().createDocumentFragment());
		nested.parent=this;
		nested.ns=ns;
		return nested;
	}
	public XMLDomBuilder appendParent() throws UnsupportedOperationException {
		
		if( parent == null ){
			throw new UnsupportedOperationException("No parent");
		}
		parent.frag.appendChild(getFragment());
		return parent;
	}
	public SimpleXMLBuilder getParent() {
		if( parent == null ){
			throw new UnsupportedOperationException("No parent");
		}
		return parent;
	}
	/* (non-Javadoc)
	 * @see uk.ac.ed.epcc.webapp.content.SimpleXMLBuilder#setEscapeUnicode(boolean)
	 */
	public boolean setEscapeUnicode(boolean escape_unicode) {
		// DOM stores strings in native repr
		return false;
	}

}