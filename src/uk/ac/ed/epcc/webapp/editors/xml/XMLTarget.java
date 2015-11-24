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
