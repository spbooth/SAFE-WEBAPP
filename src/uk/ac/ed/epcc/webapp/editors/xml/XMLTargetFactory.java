package uk.ac.ed.epcc.webapp.editors.xml;

import java.util.LinkedList;

import javax.xml.validation.Schema;

import uk.ac.ed.epcc.webapp.AppContext;
import uk.ac.ed.epcc.webapp.Contexed;

/** Interface to locate an {@link XMLTarget}
 * based on a location path.
 * The number of path elements used to identify the document
 * and the number that represent a position within the document
 * is implementation dependent.
 * 
 * The first element of the path should always be the 
 * tag used to to create the {@link XMLTargetFactory} from the 
 * {@link AppContext#makeObject(Class, String)} call. This way paths are
 * unique and can be used to locate the correct factory.
 * @author spb
 *
 */
public interface XMLTargetFactory extends Contexed{
	/** Get the XMLTarget represented by the path.
	 * 
	 * @param location
	 * @return XMLTarget
	 */
	public XMLTarget find(LinkedList<String> location);
	/** Get the schema to validate content.
	 * 
	 * @return Schema or null;
	 */
	public Schema getSchema();
	/** Get a DOMVisitor that performs additional
	 * (non-schema) validation throwing exceptions that
	 * should  be added to the schema errors.
	 * 
	 * @return DOMVisitor or null;
	 */
	public DomVisitor getValidatingVisitor();
}
