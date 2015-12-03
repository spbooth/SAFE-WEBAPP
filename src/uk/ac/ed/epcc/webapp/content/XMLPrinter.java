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