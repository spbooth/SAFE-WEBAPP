// Copyright - The University of Edinburgh 2011
/*******************************************************************************
 * Copyright (c) - The University of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.webapp.content;

import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;
/** SimpleXML Builder that generates a String representation
 * 
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: XMLPrinter.java,v 1.5 2015/07/07 15:35:49 spb Exp $")
public class XMLPrinter extends AbstractXMLBuilder {

	protected StringBuilder sb;
	
	private AbstractXMLBuilder parent=null;
	

	public XMLPrinter() {
		super();
		sb = new StringBuilder();
		
	}
	public XMLPrinter(AbstractXMLBuilder parent){
		this();
		this.parent=parent;
		setValidXML(parent.getValidXML());
		setEscapeUnicode(parent.getEscapeUnicode());
	}
   
	
	public AbstractXMLBuilder getParent(){
		return parent;
	}
	
	@Override
	public String toString() {
		if( ! matched()){
			throw new ConsistencyError("unclosed tag "+getTags().peek()+" "+sb.toString());
		}
		  return sb.toString();
	  }

	

	public void append(XMLPrinter hb) {
		if( ! hb.matched()){
			throw new ConsistencyError("unclosed tag "+hb.getTags().peek());
		}
		endOpen();
		sb.append(hb.sb);
	}

	





	@Override
	public void clear() {
		super.clear();
		sb=new StringBuilder();
	}





	public SimpleXMLBuilder getNested() throws UnsupportedOperationException {
		try {
			XMLPrinter nested = getClass().newInstance();
			nested.parent=this;
			return nested;
		} catch (Exception e) {
			throw new UnsupportedOperationException(e);
		}
	}





	public AbstractXMLBuilder appendParent() throws UnsupportedOperationException {
		if( parent == null ){
			throw new UnsupportedOperationException("No parent");
		}
		if( ! matched()){
			throw new ConsistencyError("unclosed tag "+getTags().peek());
		}
		parent.endOpen();
		parent.append(sb.toString());
		return parent;
	}
	@Override
	protected void append(CharSequence s) {
		sb.append(s);
	}
	@Override
	protected void append(char s) {
		sb.append(s);
	}

	
}