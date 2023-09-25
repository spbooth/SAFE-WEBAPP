package uk.ac.ed.epcc.webapp.forms.inputs;

import uk.ac.ed.epcc.webapp.exceptions.ConsistencyError;

/** This is an input that can either be presented as a {@link ListInput} or an {@link AutoComplete}.
 * 
 * An implementing class needs to select which presentation to use in the #{@link #accept(InputVisitor)} method.
 * (e.g. based on a preference or the number of suggestions).
 * 
 * 
 * @param <V>
 * @param <T>
 */
public interface AutoCompleteListInput<V,T> extends ListInput<V, T>, AutoComplete<V, T> {

	/** Should this input be presented as a list or an auto-complete
	 * 
	 * @return
	 */
	public boolean useListPresentation();
	public default  <R> R accept(InputVisitor<R> vis) throws Exception{
		if( useListPresentation()) {
			return vis.visitListInput(this);
		}else {
			return vis.visitAutoCompleteInput(this);
		}
	}
}
