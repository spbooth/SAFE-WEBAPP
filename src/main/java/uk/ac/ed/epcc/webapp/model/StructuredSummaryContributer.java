package uk.ac.ed.epcc.webapp.model;

import java.util.Set;

import uk.ac.ed.epcc.webapp.model.data.DataObject;
/** An extended form of {@link SummaryContributer}
 * 
 * This includes a 
 * 
 * @author Stephen Booth
 *
 * @param <T>
 */
public interface StructuredSummaryContributer<T extends DataObject> extends SummaryContributer<T> {

	/** Get a set of attributes that could be generated. 
	 * Values returned from this method are the keys from the {@link #addAttributes(java.util.Map, DataObject)}
	 * call.
	 * 
	 * This is intended to help format a table of targets with attribute columns.
	 * Therefore attributes that will never be generated should be omitted.
	 * Not all possible attributes need be returned by this call
	 * 
	 * @return
	 */
	public void addSummaryFields(Set<String> attributes);
}
