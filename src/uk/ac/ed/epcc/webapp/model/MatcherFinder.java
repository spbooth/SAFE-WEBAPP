package uk.ac.ed.epcc.webapp.model;

import java.util.Set;

import uk.ac.ed.epcc.webapp.model.data.DataObject;

/** Similar to {@link NameFinder} this interface supports a 
 * many-to-one name lookup. Where {@linkNameFinder} is intended to lookup
 * an object by name. This is intended to looup an owning object by the name of
 * a client object.
 * 
 * @author spb
 *
 * @param <T>
 */
public interface MatcherFinder<T extends DataObject & Matcher> {
	public abstract T findOwner(String clientName);
	public Set<T> getOwners();
}
