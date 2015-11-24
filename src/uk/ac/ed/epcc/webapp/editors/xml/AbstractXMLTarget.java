// Copyright - The University of Edinburgh 2012
package uk.ac.ed.epcc.webapp.editors.xml;

import java.util.LinkedList;

import javax.xml.validation.Schema;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.jdbc.exception.DataException;
import uk.ac.ed.epcc.webapp.model.xml.XMLOverlay;
import uk.ac.ed.epcc.webapp.model.xml.XMLOverlay.XMLFile;
import uk.ac.ed.epcc.webapp.session.SessionService;

/** Default implementation of {@link XMLTarget}
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Id: AbstractXMLTarget.java,v 1.3 2014/09/15 14:30:16 spb Exp $")
public abstract class AbstractXMLTarget implements XMLTarget {

	private LinkedList<String> path;
	
	public AbstractXMLTarget(LinkedList<String> path){
		this.path=new LinkedList<String>(path);
		
	}

	
	
	public final LinkedList<String> getTargetPath() {
		return new LinkedList<String>(path);
	}

	protected abstract LinkedList<String> extractPrefix(LinkedList<String> node_path);
	public final Node getTargetNode() {
		LinkedList<String> node_path=new LinkedList<String>(path);
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
