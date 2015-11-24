// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp.content;


/** Interface for extended XML content (including HTML).
 * 
 * 
 * 
 * @author spb
 *
 */


public interface ExtendedXMLBuilder extends SimpleXMLBuilder{

	/** Insert a non-breaking space.
	 * 
	 */
	public abstract void nbs();

	/** Insert line break.
	 * 
	 */
	public abstract void br();
	
}