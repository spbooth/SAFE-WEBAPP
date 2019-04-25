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

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import uk.ac.ed.epcc.webapp.Contexed;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** Interface representing the target of XML edit transitions
 * This represents both the Document being edited and a target position
 * in that document.
 * 
 * Note that edits may result in changes to the location paths of children/siblings of the edit location so
 * XMLTargets (including child/parent objects) should be discarded and
 * re-aquired after each edit operation.
 * 
 * @author spb
 *
 */
public interface XMLTarget extends Contexed{
	/** Get the {@link XMLTargetFactory} for this object.
	 * 
	 * @return XMLTargetFactory
	 */
	public XMLTargetFactory getXMLTargetFactory();
	/** get the Document being edited
	 * 
	 * @return Document
	 */
	public Document getDocument();
	/** Get the XMLTarget corresponding to the Document root element.
	 * 
	 * @return XMLTarget
	 */
	public XMLTarget getRootTarget();
	/** Get the target path selected 
	 *  
	 * @return LinkedList<String>
	 */
	public LinkedList<String> getTargetPath();
	/** Get the Node referenced by the target path.
	 * 
	 * @return Node
	 */
	public Node getTargetNode();
	
	/** Can the current person view this target.
	 * 
	 * @param sess
	 * @return boolean
	 */
	public boolean canView(SessionService<?> sess);
	/** commits any change to the document
	 * 
	 * @throws Exception
	 */
	public void commit() throws Exception;
	
}