package uk.ac.ed.epcc.webapp.model;

import java.util.Map;

import uk.ac.ed.epcc.webapp.content.Table;
import uk.ac.ed.epcc.webapp.model.data.DataObject;

/** marker interface for {@link SummaryContributer}s that also add columns to a table index
 * 
 * @author spb
 *
 * @param <T>
 */
public interface IndexTableContributor<T extends DataObject> extends SummaryContributer<T> {

	
	/** Add to the set of index attributes.
	 * 
	 * These will usually be added to a {@link Table} so the data should be of a
	 * type that can be displayed by {@link Table}s
	 * 
	 * The keys should be display text.
	 * 
	 * This defaults to the same attributes as {@link SummaryContributer} but the method can
	 * be overidden to show different attributes in the two contexts
	 * 
	 * @param attributes
	 * @param target
	 */
	public default void addIndexAttributes(Map<String,Object> attributes,T target) {
		addAttributes(attributes, target);
	}
}
