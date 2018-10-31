//| Copyright - The University of Edinburgh 2012                            |
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

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/** Default implementation of {@link XMLTarget}
 * @author spb
 *
 */

public abstract class AbstractXMLTarget implements XMLTarget {

	private LinkedList<String> path;
	
	public AbstractXMLTarget(LinkedList<String> path){
		this.path=new LinkedList<>(path);
		
	}

	
	
	public final LinkedList<String> getTargetPath() {
		return new LinkedList<>(path);
	}

	protected abstract LinkedList<String> extractPrefix(LinkedList<String> node_path);
	public final Node getTargetNode() {
		LinkedList<String> node_path=new LinkedList<>(path);
		LinkedList<String> prefix_path = extractPrefix(node_path);
		LocatorDomVisitor v = new LocatorDomVisitor(node_path);
		DomWalker walker = new DomWalker();
		Document document = getDocument();
		if( document==null){
			return null;
		}
		walker.visitElement(document.getDocumentElement(), prefix_path, v);
		return v.getNode();
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		return result;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractXMLTarget other = (AbstractXMLTarget) obj;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		return true;
	}

}