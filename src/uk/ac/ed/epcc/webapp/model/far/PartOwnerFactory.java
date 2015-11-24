// Copyright - The University of Edinburgh 2015
package uk.ac.ed.epcc.webapp.model.far;

import uk.ac.ed.epcc.webapp.model.data.DataObjectFactory;

/** Abstract factory for {@link PartOwner}s
 * @author spb
 *
 */
@uk.ac.ed.epcc.webapp.Version("$Revision: 1.2 $")
public abstract class PartOwnerFactory<O extends PartOwner> extends DataObjectFactory<O> {

	/** Get the {@link PartManager} for the next level of 
	 * the hierarchy. This always returns null for
	 * the bottom layer.
	 * 
	 * @return {@link PartManager} or null
	 */
	public abstract PartManager getChildManager();
}
