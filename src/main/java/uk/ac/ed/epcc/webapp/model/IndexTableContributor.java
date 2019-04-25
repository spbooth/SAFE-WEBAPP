package uk.ac.ed.epcc.webapp.model;

import uk.ac.ed.epcc.webapp.model.data.DataObject;
import uk.ac.ed.epcc.webapp.model.data.forms.registry.SummaryContentProvider;

/** marker interface for {@link SummaryContentProvider}s that also add columns to a table index
 * 
 * @author spb
 *
 * @param <T>
 */
public interface IndexTableContributor<T extends DataObject> extends SummaryContributer<T> {

}
