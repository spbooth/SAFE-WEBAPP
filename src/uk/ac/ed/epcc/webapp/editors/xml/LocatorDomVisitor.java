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
import org.w3c.dom.Node;
import org.w3c.dom.Text;
/** A {@link DomVisitor} that locates a target {@link Node} in the
 * {@link org.w3c.dom.Document} being walked using a location path.
 * 
 * @author spb
 *
 */
public class LocatorDomVisitor implements DomVisitor {
	private LinkedList<String> target_path;
	private Node target=null;
	
	public LocatorDomVisitor(LinkedList<String> path){
		this.target_path=new LinkedList<String>(path);
	}
	public Node getNode(){
		return target;
	}
	
	public boolean beginStartElement(Element e, LinkedList<String> path) {
		if( target_path == null ){
			return false;
		}
		if( target_path.size() == 0){
			target_path=null;
			return false; // no need to recurse futher we found it.
		}
		String next_step = target_path.getFirst();
		final String last = path.getLast();
		if( last.equals(next_step)){
			target_path.removeFirst();
			if( target_path.size() == 0){
				target=e;
				target_path=null;
				return false;
			}
			return true;
		}
		return false;
	}

	
	public boolean useAttributes(Element e, LinkedList<String> path) {
		if( target_path == null || target_path.size() == 0 ){
			return false;
		}
		if( DomWalker.isAttribute(target_path.getFirst())){
			return true;
		}
		return false;
	}

	
	public void endStartElement(Element e, LinkedList<String> path) {

	}

	
	public void endElement(Element e, LinkedList<String> path) {
		// been down this path don't select anything else.
		target_path=null;
	}

	
	public void textNode(Text n, LinkedList<String> path) {
		if(target_path != null &&  target_path.size() == 1 && target_path.getFirst().equals(path.getLast()) ){
			target=n;
			target_path=null;
		}
	}
	
	public void commentNode(Comment n, LinkedList<String> path) {
		if(target_path != null &&  target_path.size() == 1 && target_path.getFirst().equals(path.getLast()) ){
			target=n;
			target_path=null;
		}
	}
	
	public void visitAttr(Attr a, LinkedList<String> path) {
		if(target_path != null &&  target_path.size() == 1 && target_path.getFirst().equals(path.getLast()) ){
			target=a;
			target_path=null;
		}

	}

}