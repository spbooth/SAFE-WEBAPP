// Copyright - The University of Edinburgh 2011
package uk.ac.ed.epcc.webapp;
/** Interface for {@link Contexed} objects that can be queried for their
 * configuration tag. Usually this is the database table.
 * If an object implements this interface then a call to {@link AppContext#getClassFromName}
 * should either fail or resolve to the tagged class.
 * 
 * @author spb
 *
 */
public interface Tagged extends Contexed {
	/** get the construction tag
	 * 
	 * @return String tag
	 */
	public String getTag();
}