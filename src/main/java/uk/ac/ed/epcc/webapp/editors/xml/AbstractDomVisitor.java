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