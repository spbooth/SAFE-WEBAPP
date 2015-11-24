// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp.content;


/** Interface for objects that can add themselves to an {@link SimpleXMLBuilder}
 * 
 * @author spb
 *
 */
public interface XMLGenerator {
	public SimpleXMLBuilder addContent(SimpleXMLBuilder builder);
}