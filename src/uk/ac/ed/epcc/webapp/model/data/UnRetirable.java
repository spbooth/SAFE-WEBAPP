// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.model.data;

/** Interface for {@link Retirable} objects where this can be reversed.
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.1 $")
public interface UnRetirable extends Retirable {
	
	/** Can the object be un-retired.
	 * 
	 * @return
	 */
	public boolean canRestore();
	
	/**
	 * restore a retired this object.
	 * 
	 * @throws Exception
	 */
	public void restore() throws Exception;

}
